(function () {
    const root = document.getElementById('chat-widget-root');
    if (!root) return;

    window.PetChat = window.PetChat || {};

    let threadId = null;
    let stomp = null;
    let currentUserId = null;
    let petId = null;

    let subscription = null;
    let subscribedThreadId = null;

    // Header info
    let headerInfo = {name: 'Chat', avatar: null, petName: null, location: ''};

    // --- helpers ---
    const boxEl = () => document.getElementById('chatMessages');

    function scrollToBottom() {
        const b = boxEl();
        if (!b) return;
        b.scrollTop = b.scrollHeight;
        requestAnimationFrame(() => {
            b.scrollTop = b.scrollHeight;
            setTimeout(() => {
                b.scrollTop = b.scrollHeight;
            }, 0);
        });
    }

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
        if (sameDay) return hm;
        const dm = d.toLocaleDateString('en-US', {day: '2-digit', month: 'short'});
        return `${dm}, ${hm}`;
    }

    function createUI() {
        if (!document.getElementById('chatWidget')) {
            root.innerHTML = `
        <div class="chat-widget" id="chatWidget" style="display:none">
          <div class="chat-header">
            <div class="left">
              <div class="avatar" id="chatHeaderAvatar"></div>
              <div>
                <div id="chatHeaderName">Chat</div>
                <div id="chatHeaderSub" class="sub"></div>
              </div>
            </div>
            <button class="chat-close" id="chatCloseBtn" title="Close">&times;</button>
          </div>
          <div class="chat-messages" id="chatMessages"></div>
          <div class="chat-input">
            <textarea id="chatText" placeholder="Type a message..."></textarea>
            <button id="chatSend">Send</button>
          </div>
        </div>
      `;
            document.getElementById('chatCloseBtn').addEventListener('click', () => {
                document.getElementById('chatWidget').style.display = 'none';
            });
            document.getElementById('chatSend').addEventListener('click', sendMessage);
            document.getElementById('chatText').addEventListener('keydown', (e) => {
                if (e.key === 'Enter' && !e.shiftKey) {
                    e.preventDefault();
                    sendMessage();
                }
            });
        }
        applyHeader();
    }

    function applyHeader() {
        const nameEl = document.getElementById('chatHeaderName');
        const subEl = document.getElementById('chatHeaderSub');
        const avEl = document.getElementById('chatHeaderAvatar');

        if (nameEl) nameEl.textContent = headerInfo.name || 'Chat';
        if (subEl) {
            const loc = headerInfo.location ? headerInfo.location : '';
            const pet = headerInfo.petName ? `About ${headerInfo.petName}` : '';
            subEl.textContent = loc && pet ? `${loc} • ${pet}` : (loc || pet || '');
        }
        if (avEl) {
            avEl.innerHTML = '';
            if (headerInfo.avatar) {
                const img = document.createElement('img');
                img.src = headerInfo.avatar;
                img.alt = 'avatar';
                img.style.width = '100%';
                img.style.height = '100%';
                img.style.objectFit = 'cover';
                avEl.appendChild(img);
            } else {
                const span = document.createElement('span');
                span.style.fontWeight = '700';
                span.style.color = '#374151';
                span.textContent = (headerInfo.name || 'U').trim().charAt(0).toUpperCase();
                avEl.appendChild(span);
            }
        }
    }

    function showWidget() {
        createUI();
        document.getElementById('chatWidget').style.display = 'flex';
        scrollToBottom();
    }

    async function ensureThread() {
        if (threadId) return threadId;
        if (!petId) throw new Error('petId missing for thread creation');
        const res = await fetch('/chat/thread', {
            method: 'POST',
            headers: {'Content-Type': 'application/json'},
            body: JSON.stringify({petId})
        });
        const data = await res.json();
        threadId = data.threadId;
        return threadId;
    }

    async function loadHistory(limit = 500) {
        createUI();
        const res = await fetch(`/chat/${threadId}/messages?limit=${limit}`);
        const data = await res.json();
        const box = boxEl();
        box.innerHTML = '';
        lastSenderId = null;
        data.forEach(appendMessage);
        scrollToBottom();
    }

    // compact grouping
    let lastSenderId = null;

    function appendMessage(m) {
        const box = boxEl();
        const mine = Number(m.senderId) === Number(currentUserId);

        const row = document.createElement('div');
        row.className = 'msg-row ' + (mine ? 'me' : 'other');
        if (lastSenderId === m.senderId) row.classList.add('compact');

        const wrap = document.createElement('div');
        wrap.className = 'msg-wrap';

        const bubble = document.createElement('div');
        bubble.className = 'msg-bubble ' + (mine ? 'me' : '');
        bubble.textContent = (m.content || '').replace(/\s+$/g, '');

        const meta = document.createElement('div');
        meta.className = 'msg-time';
        meta.textContent = formatTime(m.sentAt);

        wrap.appendChild(bubble);
        wrap.appendChild(meta);
        row.appendChild(wrap);
        box.appendChild(row);

        lastSenderId = m.senderId;
        scrollToBottom();
    }

    function subscribeToCurrentThread() {
        if (subscription) {
            try {
                subscription.unsubscribe();
            } catch (_) {
            }
        }
        subscription = stomp.subscribe(`/topic/chats.${threadId}`, (frame) => {
            const payload = JSON.parse(frame.body);
            appendMessage(payload);
        });
        subscribedThreadId = threadId;
    }

    function connectStomp() {
        if (stomp && stomp.connected) {
            if (subscribedThreadId !== threadId) subscribeToCurrentThread();
            requestAnimationFrame(scrollToBottom);
            return;
        }
        const socket = new SockJS('/ws');
        stomp = Stomp.over(socket);
        // stomp.debug = null;
        stomp.connect({}, () => {
            subscribeToCurrentThread();
            requestAnimationFrame(scrollToBottom);
        });
    }

    function sendMessage() {
        const input = document.getElementById('chatText');
        if (!input) return;
        const content = input.value.trim();
        if (!content) return;
        stomp.send('/app/chat.send', {}, JSON.stringify({
            threadId,
            senderId: currentUserId,
            content
        }));
        input.value = '';
    }

    // Entry from pet details
    const btn = document.getElementById('chatWithOwnerBtn');
    if (btn) {
        currentUserId = Number(btn.dataset.currentUserId);
        petId = Number(btn.dataset.petId);
        const ownerName = btn.dataset.ownerName || 'Owner';
        const ownerAvatar = btn.dataset.ownerAvatar || null;
        const ownerLocation = btn.dataset.ownerLocation || '';
        const petName = btn.dataset.petName || null;

        headerInfo = {name: ownerName, avatar: ownerAvatar, location: ownerLocation, petName};

        btn.addEventListener('click', async () => {
            try {
                await ensureThread();
                await loadHistory();
                showWidget();
                connectStomp();
            } catch (e) {
                console.error('Chat open error (details):', e);
            }
        });
    }

    // Optional entry from other pages
    window.PetChat.openThread = async function (threadIdArg, currentUserIdArg, header) {
        try {
            currentUserId = Number(currentUserIdArg);
            threadId = Number(threadIdArg);
            headerInfo = Object.assign({name: 'Chat', avatar: null, petName: null, location: ''}, header || {});
            await loadHistory();
            showWidget();
            connectStomp();
        } catch (e) {
            console.error('Chat open error (inbox):', e);
        }
    };

    window.addEventListener('beforeunload', () => {
        if (subscription) {
            try {
                subscription.unsubscribe();
            } catch (_) {
            }
        }
        if (stomp && stomp.connected) {
            try {
                stomp.disconnect(() => {
                });
            } catch (_) {
            }
        }
    });
})();
