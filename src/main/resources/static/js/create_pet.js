// Toggle price visibility for "FOR_ADOPTION"
document.addEventListener('DOMContentLoaded', () => {
    const statusEl = document.getElementById('status');
    const priceField = document.getElementById('priceField');
    const desc = document.getElementById('description');
    const counter = document.getElementById('desc-count');

    const applyPriceVisibility = () => {
        const val = statusEl?.value || '';
        const hide = val === 'FOR_ADOPTION';
        if (hide) {
            priceField.style.display = 'none';
        } else {
            priceField.style.display = '';
        }
    };
    statusEl?.addEventListener('change', applyPriceVisibility);
    applyPriceVisibility();

    const maxLen = 500;
    const updateCount = () => {
        const len = (desc?.value || '').length;
        counter.textContent = `${len} / ${maxLen}`;
        if (len > maxLen) counter.style.color = 'var(--danger)'; else counter.style.color = 'var(--muted)';
    };
    desc?.addEventListener('input', updateCount);
    updateCount();
});
