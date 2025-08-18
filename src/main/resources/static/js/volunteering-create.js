document.addEventListener('DOMContentLoaded', () => {
    const form = document.getElementById('opportunity-form');
    if (!form) return;
  
    const title = document.getElementById('title');
    const type = document.getElementById('type');
    const location = document.getElementById('location');
    const startDate = document.getElementById('startDate');
    const endDate = document.getElementById('endDate');
    const description = document.getElementById('description');
  
    const show = (id) => { const el = document.getElementById(id); if (el) el.style.display = 'block'; };
    const hide = (id) => { const el = document.getElementById(id); if (el) el.style.display = 'none'; };
  
    form.addEventListener('submit', (e) => {
      // Client validation (server still validates)
      let ok = true;
  
      const req = [
        ['title', title],
        ['type', type],
        ['location', location],
        ['startDate', startDate],
        ['endDate', endDate],
        ['description', description],
        ['status', document.getElementById('status')],
      ];
  
      req.forEach(([name, el]) => {
        if (!el || !el.value) {
          show(name + '-error'); ok = false;
        } else {
          hide(name + '-error');
        }
      });
  
      if (startDate.value && endDate.value && endDate.value < startDate.value) {
        ok = false;
        const err = document.getElementById('endDate-error');
        if (err) { err.textContent = 'End date cannot be before start date'; err.style.display = 'block'; }
      }
  
      if (!ok) e.preventDefault();
    });
  });
  