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

    // Header data now also includes location
    let headerInfo = {name: 'Chat', avatar: null, petName: null, location: ''};

    function createUI() {
        if (!document.getElementById('chatWidget')) {
            root.innerHTML = `
        <div class="chat-widget" id="chatWidget" style="display:none">
          <div class="chat-header">
            <div style="display:flex;align-items:center;gap:10px;">
              <div class="avatar" id="chatHeaderAvatar"
                   style="width:28px;height:28px;border-radius:999px;background:#e5e7eb;overflow:hidden;display:flex;align-items:center;justify-content:center;"></div>
              <div>
                <div id="chatHeaderName" style="font-weight:600">Chat</div>
                <div id="chatHeaderSub" class="muted" style="font-size:12px;"></div>
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
            // Show "Location • About {pet}"
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
                // Fallback: initial badge
                const span = document.createElement('span');
                span.style.fontWeight = '700';
                span.style.color = '#374151';
                span.textContent = (headerInfo.name || 'U').trim().charAt(0).toLowerCase();
                avEl.appendChild(span);
            }
        }
    }

    function showWidget() {
        createUI();
        document.getElementById('chatWidget').style.display = 'flex';
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

    async function loadHistory(limit = 50) {
        createUI();
        const res = await fetch(`/chat/${threadId}/messages?limit=${limit}`);
        const data = await res.json();
        const box = document.getElementById('chatMessages');
        box.innerHTML = '';
        data.forEach(m => appendMessage(m));
        box.scrollTop = box.scrollHeight;
    }

    function appendMessage(m) {
        const box = document.getElementById('chatMessages');
        const div = document.createElement('div');
        div.className = 'msg ' + (Number(m.senderId) === Number(currentUserId) ? 'me' : 'them');
        div.textContent = m.content;
        box.appendChild(div);
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
            const box = document.getElementById('chatMessages');
            box.scrollTop = box.scrollHeight;
        });
        subscribedThreadId = threadId;
    }

    function connectStomp() {
        if (stomp && stomp.connected) {
            if (subscribedThreadId !== threadId) subscribeToCurrentThread();
            return;
        }
        const socket = new SockJS('/ws');
        stomp = Stomp.over(socket);
        // stomp.debug = null;
        stomp.connect({}, () => subscribeToCurrentThread());
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

    // Entry A: opened from pet details (non-owner)
    const btn = document.getElementById('chatWithOwnerBtn');
    if (btn) {
        currentUserId = Number(btn.dataset.currentUserId);
        petId = Number(btn.dataset.petId);
        const ownerName = btn.dataset.ownerName || 'Owner';
        const ownerAvatar = btn.dataset.ownerAvatar || null;
        const ownerLocation = btn.dataset.ownerLocation || '';
        const petName = btn.dataset.petName || null;

        headerInfo = {name: ownerName, avatar: ownerAvatar, location: ownerLocation, petName: petName};

        btn.addEventListener('click', async () => {
            try {
                await ensureThread();
                await loadHistory();
                connectStomp();
                showWidget();
            } catch (e) {
                console.error('Chat open error (details):', e);
            }
        });
    }

    // Entry B: from inbox (kept for completeness)
    window.PetChat.openThread = async function (threadIdArg, currentUserIdArg, header) {
        try {
            currentUserId = Number(currentUserIdArg);
            threadId = Number(threadIdArg);
            headerInfo = Object.assign({name: 'Chat', avatar: null, petName: null, location: ''}, header || {});
            await loadHistory();
            connectStomp();
            showWidget();
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
