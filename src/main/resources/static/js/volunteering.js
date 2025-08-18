document.addEventListener('DOMContentLoaded', () => {
    // update results count if server didn’t set it (fallback)
    const container = document.querySelector('#opportunities-container');
    const countEl = document.querySelector('#results-count');
    if (container && countEl && !countEl.textContent.includes('result')) {
      const cards = container.querySelectorAll('.opportunity-card').length;
      countEl.textContent = `${cards} result${cards === 1 ? '' : 's'}`;
    }
  });
  