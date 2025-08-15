(function () {
    const boot = window.MESSENGER_BOOT || {currentUserId: 0, preselectFirst: false};
    const me = Number(boot.currentUserId || 0);

    // DOM
    const list = document.getElementById('threadList');
    const filter = document.getElementById('chatFilter');

    const headerAvatarWrap = document.getElementById('convAvatar');
    const headerName = document.getElementById('convName');
    const headerSub = document.getElementById('convSub');

    const msgBox = document.getElementById('convMessages');
    const emptyHint = document.getElementById('emptyHint');
    const input = document.getElementById('convInput');
    const sendBtn = document.getElementById('convSend');

    // STOMP
    let stomp = null;
    let subscription = null;
    let currentThreadId = null;

    // Cache last sender to compact consecutive messages
    let lastSenderId = null;

    // --- util: format time in AM/PM ---
    function formatTime(isoLike) {
        if (!isoLike) return '';
        const d = new Date(isoLike.replace(' ', 'T'));
        if (isNaN(d.getTime())) return '';
        const now = new Date();
        const sameDay =
            d.getFullYear() === now.getFullYear() &&
            d.getMonth() === now.getMonth() &&
            d.getDate() === now.getDate();

        const hm = d.toLocaleTimeString('en-US', {hour: 'numeric', minute: '2-digit', hour12: true});
        if (sameDay) return hm; // e.g., "9:35 PM"

        const dayMonth = d.toLocaleDateString('en-US', {day: '2-digit', month: 'short'}); // "14 Aug"
        return `${dayMonth}, ${hm}`; // "14 Aug, 9:35 PM"
    }

    function setHeader(name, avatar, pet) {
        headerName.textContent = name || 'Conversation';
        headerSub.textContent = pet ? ('About ' + pet) : '';

        headerAvatarWrap.innerHTML = '';
        const av = document.createElement('div');
        av.className = 'avatar';
        if (avatar) {
            const img = document.createElement('img');
            img.src = avatar;
            img.alt = 'avatar';
            img.style.width = '100%';
            img.style.height = '100%';
            img.style.objectFit = 'cover';
            av.appendChild(img);
        } else {
            const sp = document.createElement('span');
            sp.className = 'avatar-fallback';
            sp.textContent = (name || 'U').slice(0, 1);
            av.appendChild(sp);
        }
        headerAvatarWrap.appendChild(av);
    }

    function appendMessage(m) {
        const mine = Number(m.senderId) === me;

        const row = document.createElement('div');
        row.className = 'row' + (mine ? ' me' : ' other');
        if (lastSenderId === m.senderId) row.classList.add('compact');  // tighter stack

        const wrap = document.createElement('div');
        wrap.className = 'wrap';

        const bubble = document.createElement('div');
        bubble.className = 'msg' + (mine ? ' me' : '');
        // Trim trailing whitespace/newlines to avoid extra vertical space
        const cleaned = (m.content || '').replace(/\s+$/g, '');
        bubble.textContent = cleaned;

        const meta = document.createElement('div');
        meta.className = 'msg-meta';
        meta.textContent = formatTime(m.sentAt);

        wrap.appendChild(bubble);
        wrap.appendChild(meta);
        row.appendChild(wrap);

        msgBox.appendChild(row);
        msgBox.scrollTop = msgBox.scrollHeight;
        if (emptyHint) emptyHint.style.display = 'none';

        lastSenderId = m.senderId;
    }

    async function loadHistory(threadId) {
        const res = await fetch(`/chat/${threadId}/messages?limit=500`);
        const data = await res.json();
        msgBox.innerHTML = '';
        lastSenderId = null; // reset grouping
        if (!Array.isArray(data) || data.length === 0) {
            if (emptyHint) emptyHint.style.display = 'block';
            return;
        }
        data.forEach(appendMessage);
        if (emptyHint) emptyHint.style.display = 'none';
    }

    function ensureConnected(cb) {
        if (stomp && stomp.connected) {
            cb();
            return;
        }
        const socket = new SockJS('/ws');
        stomp = Stomp.over(socket);
        // stomp.debug = null;
        stomp.connect({}, cb);
    }

    function resubscribe(threadId) {
        if (subscription) {
            try {
                subscription.unsubscribe();
            } catch (_) {
            }
            subscription = null;
        }
        subscription = stomp.subscribe(`/topic/chats.${threadId}`, (frame) => {
            const payload = JSON.parse(frame.body);
            appendMessage(payload);
        });
    }

    async function openThreadFromEl(el) {
        document.querySelectorAll('.thread-row').forEach(n => n.classList.remove('active'));
        el.classList.add('active');

        currentThreadId = Number(el.dataset.threadId);
        const otherName = el.dataset.otherName || null;
        const otherAvatar = el.dataset.otherAvatar || null;
        const petName = el.dataset.petName || null;

        setHeader(otherName, otherAvatar, petName);
        await loadHistory(currentThreadId);
        ensureConnected(() => resubscribe(currentThreadId));
    }

    // Click on a thread
    list.addEventListener('click', (e) => {
        const row = e.target.closest('.thread-row');
        if (!row) return;
        openThreadFromEl(row).catch(console.error);
    });

    // Filter
    filter.addEventListener('input', () => {
        const q = filter.value.toLowerCase();
        list.querySelectorAll('.thread-row').forEach(el => {
            const name = (el.dataset.otherName || '').toLowerCase();
            const pet = (el.dataset.petName || '').toLowerCase();
            el.style.display = (name.includes(q) || pet.includes(q)) ? '' : 'none';
        });
    });

    // Send
    function send() {
        const text = (input.value || '').trim();
        if (!text || !currentThreadId) return;
        stomp.send('/app/chat.send', {}, JSON.stringify({
            threadId: currentThreadId,
            senderId: me,
            content: text
        }));
        input.value = '';
        // rely on server echo (avoids duplicates)
    }

    sendBtn.addEventListener('click', send);
    input.addEventListener('keydown', (e) => {
        if (e.key === 'Enter' && !e.shiftKey) {
            e.preventDefault();
            send();
        }
    });

    // Preselect first thread
    if (boot.preselectFirst) {
        const first = list.querySelector('.thread-row');
        if (first) openThreadFromEl(first).catch(console.error);
    }
})();
