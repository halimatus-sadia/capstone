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

    // Metadata
    let otherName = null, otherAvatar = null, petName = null;

    // Helpers
    function clearConversation() {
        msgBox.innerHTML = '';
        if (emptyHint) emptyHint.style.display = 'block';
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
        const row = document.createElement('div');
        const mine = Number(m.senderId) === me;
        row.className = 'row' + (mine ? ' me' : '');
        const bubble = document.createElement('div');
        bubble.className = 'msg' + (mine ? ' me' : '');
        bubble.textContent = m.content;
        row.appendChild(bubble);
        msgBox.appendChild(row);
        msgBox.scrollTop = msgBox.scrollHeight;   // keep bottom in view
        if (emptyHint) emptyHint.style.display = 'none';
    }

    async function loadHistory(threadId) {
        const res = await fetch(`/chat/${threadId}/messages?limit=500`);
        const data = await res.json();
        msgBox.innerHTML = '';
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
        // active state
        document.querySelectorAll('.thread-row').forEach(n => n.classList.remove('active'));
        el.classList.add('active');

        // meta
        currentThreadId = Number(el.dataset.threadId);
        otherName = el.dataset.otherName || null;
        otherAvatar = el.dataset.otherAvatar || null;
        petName = el.dataset.petName || null;

        setHeader(otherName, otherAvatar, petName);
        await loadHistory(currentThreadId);

        ensureConnected(() => resubscribe(currentThreadId));
    }

    // Click handlers for rows
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
        // don't optimistically render; rely on server echo to avoid dupes
    }

    sendBtn.addEventListener('click', send);
    input.addEventListener('keydown', (e) => {
        if (e.key === 'Enter' && !e.shiftKey) {
            e.preventDefault();
            send();
        }
    });

    // Preselect first thread (optional)
    if (boot.preselectFirst) {
        const first = list.querySelector('.thread-row');
        if (first) openThreadFromEl(first).catch(console.error);
    }
})();
