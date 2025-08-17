document.addEventListener('DOMContentLoaded', function() {
    const opportunitiesContainer = document.getElementById('opportunities-container');
    const loadingElement = document.getElementById('loading');
    const noResultsElement = document.getElementById('no-results');
    const resultsCountElement = document.getElementById('results-count');

    // Load opportunities on page load
    loadOpportunities();

    // Handle filter form submission
    const filterForm = document.querySelector('form');
    if (filterForm) {
        filterForm.addEventListener('submit', function(e) {
            e.preventDefault();
            loadOpportunities();
        });
    }

    function loadOpportunities() {
        showLoading();
        
        // Get filter values
        const location = document.querySelector('input[name="location"]')?.value || '';
        const type = document.querySelector('select[name="type"]')?.value || '';
        const date = document.querySelector('input[name="date"]')?.value || '';

        // Build query string
        const params = new URLSearchParams();
        if (location) params.append('location', location);
        if (type) params.append('type', type);
        if (date) params.append('date', date);

        fetch(`/api/volunteer/opportunities?${params.toString()}`)
            .then(response => {
                if (!response.ok) {
                    throw new Error('Failed to fetch opportunities');
                }
                return response.json();
            })
            .then(data => {
                displayOpportunities(data);
            })
            .catch(error => {
                console.error('Error loading opportunities:', error);
                showError('Failed to load opportunities. Please try again.');
            });
    }

    function displayOpportunities(opportunities) {
        hideLoading();
        
        if (!opportunities || opportunities.length === 0) {
            showNoResults();
            return;
        }

        // Update results count
        if (resultsCountElement) {
            resultsCountElement.textContent = `${opportunities.length} result${opportunities.length !== 1 ? 's' : ''}`;
        }

        // Clear container
        opportunitiesContainer.innerHTML = '';

        // Create opportunity cards
        opportunities.forEach(opportunity => {
            const card = createOpportunityCard(opportunity);
            opportunitiesContainer.appendChild(card);
        });
    }

    function createOpportunityCard(opportunity) {
        const card = document.createElement('div');
        card.className = 'opportunity-card';
        
        const statusClass = opportunity.status === 'OPEN' ? 'badge-success' : 'badge-danger';
        const statusText = opportunity.status === 'OPEN' ? 'Open' : 'Closed';
        
        card.innerHTML = `
            <div class="opportunity-title">${escapeHtml(opportunity.title)}</div>
            <div class="opportunity-description">${escapeHtml(opportunity.description)}</div>
            <div class="opportunity-meta">
                <span class="badge badge-primary">${escapeHtml(opportunity.type)}</span>
                <span class="badge ${statusClass}">${statusText}</span>
                ${opportunity.location ? `<span class="badge badge-secondary">${escapeHtml(opportunity.location)}</span>` : ''}
                <span class="badge badge-secondary">${formatDate(opportunity.startDate)} - ${formatDate(opportunity.endDate)}</span>
            </div>
            <div class="opportunity-actions">
                <a href="/volunteering/detail/${opportunity.id}">View Details</a>
                <button onclick="applyToOpportunity(${opportunity.id})" class="btn btn-primary" ${opportunity.status !== 'OPEN' ? 'disabled' : ''}>
                    ${opportunity.status === 'OPEN' ? 'Apply' : 'Closed'}
                </button>
            </div>
        `;
        
        return card;
    }

    function applyToOpportunity(opportunityId) {
        fetch('/api/volunteer/applications', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify({
                opportunityId: opportunityId,
                message: 'I would like to volunteer for this opportunity.'
            })
        })
        .then(response => {
            if (!response.ok) {
                throw new Error('Failed to apply');
            }
            return response.json();
        })
        .then(data => {
            alert('Application submitted successfully!');
            // Reload opportunities to update UI
            loadOpportunities();
        })
        .catch(error => {
            console.error('Error applying:', error);
            alert('Failed to submit application. Please try again.');
        });
    }

    function showLoading() {
        loadingElement.style.display = 'block';
        noResultsElement.style.display = 'none';
        opportunitiesContainer.style.display = 'none';
    }

    function hideLoading() {
        loadingElement.style.display = 'none';
        opportunitiesContainer.style.display = 'grid';
    }

    function showNoResults() {
        noResultsElement.style.display = 'block';
        opportunitiesContainer.style.display = 'none';
        if (resultsCountElement) {
            resultsCountElement.textContent = '0 results';
        }
    }

    function showError(message) {
        hideLoading();
        alert(message);
    }

    function escapeHtml(text) {
        const div = document.createElement('div');
        div.textContent = text;
        return div.innerHTML;
    }

    function formatDate(dateString) {
        if (!dateString) return 'N/A';
        const date = new Date(dateString);
        return date.toLocaleDateString('en-US', { 
            month: 'short', 
            day: 'numeric',
            year: 'numeric'
        });
    }
});

// Global function for apply button
function applyToOpportunity(opportunityId) {
    // This function is called from the HTML onclick attribute
    // The actual implementation is in the main script above
} 