(function () {
    const root = document.getElementById('chat-widget-root');
    const btn = document.getElementById('chatWithOwnerBtn');
    if (!root || !btn) return;

    let threadId = null;
    let stomp = null;
    const currentUserId = Number(btn.dataset.currentUserId);
    const petId = Number(btn.dataset.petId);

    const createUI = () => {
        root.innerHTML = `
      <div class="chat-widget" id="chatWidget" style="display:none">
        <div class="chat-header">
          <span>Chat with Owner</span>
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
    };

    const showWidget = () => {
        document.getElementById('chatWidget').style.display = 'flex';
    };

    const ensureThread = async () => {
        if (threadId) return threadId;
        const res = await fetch('/chat/thread', {
            method: 'POST',
            headers: {'Content-Type': 'application/json'},
            body: JSON.stringify({petId})
        });
        const data = await res.json();
        threadId = data.threadId;
        return threadId;
    };

    const loadHistory = async (limit = 50) => {
        const res = await fetch(`/chat/${threadId}/messages?limit=${limit}`);
        const data = await res.json();
        const box = document.getElementById('chatMessages');
        box.innerHTML = '';
        data.forEach(m => appendMessage(m));
        box.scrollTop = box.scrollHeight;
    };

    const appendMessage = (m) => {
        const box = document.getElementById('chatMessages');
        const div = document.createElement('div');
        div.className = 'msg ' + (Number(m.senderId) === currentUserId ? 'me' : 'them');
        div.textContent = m.content;
        box.appendChild(div);
    };

    const connectStomp = () => {
        if (stomp && stomp.connected) return;
        const socket = new SockJS('/ws');
        stomp = Stomp.over(socket);
        // stomp.debug = null; // uncomment to silence logs
        stomp.connect({}, () => {
            stomp.subscribe(`/topic/chats.${threadId}`, (frame) => {
                const payload = JSON.parse(frame.body);
                appendMessage(payload);
                const box = document.getElementById('chatMessages');
                box.scrollTop = box.scrollHeight;
            });
        });
    };

    function sendMessage() {
        const input = document.getElementById('chatText');
        const content = input.value.trim();
        if (!content) return;
        stomp.send('/app/chat.send', {}, JSON.stringify({
            threadId,
            senderId: currentUserId, // IMPORTANT: send sender id
            content
        }));
        input.value = '';
    }

    // Boot
    createUI();
    btn.addEventListener('click', async () => {
        await ensureThread();
        await loadHistory();
        connectStomp();
        showWidget();
    });
})();
