document.addEventListener('DOMContentLoaded', function() {
    const applicationsContainer = document.getElementById('applications-container');
    const loadingElement = document.getElementById('loading');
    const noApplicationsElement = document.getElementById('no-applications');

    // Load user applications
    loadUserApplications();

    function loadUserApplications() {
        showLoading();
        
        fetch('/api/volunteer/applications/my-applications')
            .then(response => {
                if (!response.ok) {
                    if (response.status === 401) {
                        throw new Error('Please log in to view your applications');
                    }
                    throw new Error('Failed to load applications');
                }
                return response.json();
            })
            .then(applications => {
                displayApplications(applications);
                hideLoading();
            })
            .catch(error => {
                console.error('Error loading applications:', error);
                hideLoading();
                showError(error.message || 'Failed to load applications');
            });
    }

    function displayApplications(applications) {
        if (applications.length === 0) {
            showNoApplications();
            return;
        }

        hideNoApplications();
        applicationsContainer.innerHTML = '';

        applications.forEach(application => {
            const applicationCard = createApplicationCard(application);
            applicationsContainer.appendChild(applicationCard);
        });
    }

    function createApplicationCard(application) {
        const card = document.createElement('div');
        card.className = 'application-card';
        
        const statusClass = getStatusClass(application.status);
        const formattedDate = formatDate(application.createdAt);
        
        card.innerHTML = `
            <div class="opportunity-title">${escapeHtml(application.opportunityTitle)}</div>
            <div class="application-meta">
                <span class="application-status ${statusClass}">${application.status}</span>
                <span class="application-date">Applied: ${formattedDate}</span>
            </div>
            <div style="margin-top: 15px;">
                <a href="/volunteering/detail/${application.opportunityId}" class="btn btn-primary">View Opportunity</a>
                ${application.status === 'applied' ? '<button class="btn btn-danger withdraw-btn" data-id="' + application.id + '">Withdraw</button>' : ''}
            </div>
        `;

        // Add event listener for withdraw button
        const withdrawBtn = card.querySelector('.withdraw-btn');
        if (withdrawBtn) {
            withdrawBtn.addEventListener('click', function() {
                withdrawApplication(application.id);
            });
        }

        return card;
    }

    function getStatusClass(status) {
        switch (status) {
            case 'applied':
                return 'status-applied';
            case 'accepted':
                return 'status-accepted';
            case 'completed':
                return 'status-completed';
            default:
                return 'status-applied';
        }
    }

    function formatDate(dateString) {
        if (!dateString) return 'Unknown date';
        
        const date = new Date(dateString);
        return date.toLocaleDateString('en-US', {
            year: 'numeric',
            month: 'short',
            day: 'numeric',
            hour: '2-digit',
            minute: '2-digit'
        });
    }

    function withdrawApplication(applicationId) {
        if (!confirm('Are you sure you want to withdraw this application?')) {
            return;
        }

        fetch(`/api/volunteer/applications/${applicationId}`, {
            method: 'DELETE'
        })
        .then(response => {
            if (!response.ok) {
                throw new Error('Failed to withdraw application');
            }
            alert('Application withdrawn successfully!');
            // Reload applications
            loadUserApplications();
        })
        .catch(error => {
            console.error('Error withdrawing application:', error);
            alert('Failed to withdraw application: ' + error.message);
        });
    }

    function showLoading() {
        loadingElement.style.display = 'block';
        applicationsContainer.style.display = 'none';
        noApplicationsElement.style.display = 'none';
    }

    function hideLoading() {
        loadingElement.style.display = 'none';
        applicationsContainer.style.display = 'grid';
    }

    function showNoApplications() {
        noApplicationsElement.style.display = 'block';
        applicationsContainer.style.display = 'none';
    }

    function hideNoApplications() {
        noApplicationsElement.style.display = 'none';
        applicationsContainer.style.display = 'grid';
    }

    function showError(message) {
        // Create a simple error display
        const errorDiv = document.createElement('div');
        errorDiv.style.cssText = 'background-color: #f8d7da; color: #721c24; padding: 15px; border-radius: 5px; margin: 20px 0; text-align: center;';
        errorDiv.textContent = message;
        
        const container = document.querySelector('.applications-container');
        container.insertBefore(errorDiv, container.firstChild);
        
        // Remove error after 5 seconds
        setTimeout(() => {
            errorDiv.remove();
        }, 5000);
    }

    function escapeHtml(text) {
        const div = document.createElement('div');
        div.textContent = text;
        return div.innerHTML;
    }
}); 