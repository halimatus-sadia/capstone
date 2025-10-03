document.addEventListener('DOMContentLoaded', () => {
    const form = document.getElementById('apply-form');
    const success = document.getElementById('success-message');
    const errorBox = document.getElementById('error-message');
    const errorText = document.getElementById('error-text');
    const btn = document.getElementById('apply-btn');
  
    if (!form || !btn) return;
  
    form.addEventListener('submit', async (e) => {
      // Progressive enhancement: submit via fetch; falls back to normal POST if blocked
      e.preventDefault();
      btn.disabled = true;
  
      try {
        const resp = await fetch(form.action, { method: 'POST', headers: { 'X-Requested-With': 'fetch' } });
        if (resp.ok) {
          success.style.display = 'block';
          btn.textContent = 'Applied';
        } else {
          const text = await resp.text();
          errorText.textContent = text || 'Failed to apply.';
          errorBox.style.display = 'block';
          btn.disabled = false;
        }
      } catch (err) {
        errorText.textContent = (err && err.message) || 'Network error';
        errorBox.style.display = 'block';
        btn.disabled = false;
      }
    });
  });
  