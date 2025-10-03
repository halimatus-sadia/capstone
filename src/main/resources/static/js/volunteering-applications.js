document.addEventListener('DOMContentLoaded', () => {
    // enhance withdraw forms with a softer confirm (server still has confirm on form)
    document.querySelectorAll('form[action*="/volunteering/applications/"][action$="/withdraw"]').forEach(f => {
      f.addEventListener('submit', (e) => {
        if (!confirm('Withdraw this application?')) {
          e.preventDefault();
        }
      });
    });
  });
  