document.addEventListener('DOMContentLoaded', function() {
    // Get opportunity ID from URL
    const pathParts = window.location.pathname.split('/');
    const opportunityId = pathParts[pathParts.length - 1];
    
    if (!opportunityId || isNaN(opportunityId)) {
        showError('Invalid opportunity ID');
        return;
    }

    // Load opportunity details
    loadOpportunityDetails(opportunityId);

    function loadOpportunityDetails(id) {
        fetch(`/api/volunteer/opportunities/${id}`)
            .then(response => {
                if (!response.ok) {
                    if (response.status === 404) {
                        throw new Error('Opportunity not found');
                    }
                    throw new Error('Failed to load opportunity details');
                }
                return response.json();
            })
            .then(opportunity => {
                displayOpportunityDetails(opportunity);
            })
            .catch(error => {
                console.error('Error loading opportunity:', error);
                showError(error.message || 'Failed to load opportunity details');
            });
    }

    function displayOpportunityDetails(opportunity) {
        // Update page title
        document.title = opportunity.title + ' - Volunteer Opportunity';
        
        // Update header
        document.getElementById('opportunity-title').textContent = opportunity.title;
        document.getElementById('posted-by-name').textContent = opportunity.postedByName || 'Unknown User';
        
        // Update opportunity info
        document.getElementById('opportunity-type').textContent = opportunity.type;
        document.getElementById('opportunity-location').textContent = opportunity.location;
        document.getElementById('opportunity-date').textContent = formatDate(opportunity.date);
        
        // Update status with proper styling
        const statusElement = document.getElementById('opportunity-status');
        statusElement.textContent = opportunity.status;
        statusElement.className = `status-badge ${opportunity.status === 'open' ? 'status-open' : 'status-closed'}`;
        
        // Update description
        document.getElementById('opportunity-description').textContent = opportunity.description;
        
        // Update actions based on opportunity status and user permissions
        updateActions(opportunity);
    }

    function updateActions(opportunity) {
        const actionsContainer = document.getElementById('actions-container');
        actionsContainer.innerHTML = '';

        if (opportunity.status === 'open') {
            // Add apply button
            const applyBtn = document.createElement('button');
            applyBtn.className = 'btn btn-success';
            applyBtn.textContent = 'Apply Now';
            applyBtn.addEventListener('click', () => applyToOpportunity(opportunity.id));
            actionsContainer.appendChild(applyBtn);
        }

        // Add view applications button for opportunity poster
        // This would require checking if current user is the poster
        // For now, we'll show it to all authenticated users
        const viewApplicationsBtn = document.createElement('a');
        viewApplicationsBtn.className = 'btn btn-primary';
        viewApplicationsBtn.href = `/volunteering/applications/${opportunity.id}`;
        viewApplicationsBtn.textContent = 'View Applications';
        actionsContainer.appendChild(viewApplicationsBtn);

        // Add edit/delete buttons for opportunity poster
        // This would require checking if current user is the poster
        const editBtn = document.createElement('a');
        editBtn.className = 'btn btn-secondary';
        editBtn.href = `/volunteering/edit/${opportunity.id}`;
        editBtn.textContent = 'Edit';
        actionsContainer.appendChild(editBtn);

        const deleteBtn = document.createElement('button');
        deleteBtn.className = 'btn btn-danger';
        deleteBtn.textContent = 'Delete';
        deleteBtn.addEventListener('click', () => deleteOpportunity(opportunity.id));
        actionsContainer.appendChild(deleteBtn);
    }

    function applyToOpportunity(opportunityId) {
        const applicationData = {
            opportunityId: opportunityId,
            status: 'applied'
        };

        fetch('/api/volunteer/applications', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify(applicationData)
        })
        .then(response => {
            if (!response.ok) {
                return response.text().then(text => {
                    throw new Error(text || 'Failed to apply');
                });
            }
            return response.json();
        })
        .then(data => {
            alert('Application submitted successfully!');
            // Reload the page to update the UI
            window.location.reload();
        })
        .catch(error => {
            console.error('Error applying:', error);
            alert('Failed to submit application: ' + error.message);
        });
    }

    function deleteOpportunity(opportunityId) {
        if (!confirm('Are you sure you want to delete this opportunity? This action cannot be undone.')) {
            return;
        }

        fetch(`/api/volunteer/opportunities/${opportunityId}`, {
            method: 'DELETE'
        })
        .then(response => {
            if (!response.ok) {
                throw new Error('Failed to delete opportunity');
            }
            alert('Opportunity deleted successfully!');
            // Redirect to opportunities list
            window.location.href = '/volunteering';
        })
        .catch(error => {
            console.error('Error deleting opportunity:', error);
            alert('Failed to delete opportunity: ' + error.message);
        });
    }

    function formatDate(dateString) {
        if (!dateString) return 'Not specified';
        
        const date = new Date(dateString);
        return date.toLocaleDateString('en-US', {
            year: 'numeric',
            month: 'long',
            day: 'numeric'
        });
    }

    function showError(message) {
        const container = document.querySelector('.detail-container');
        
        // Clear existing content
        container.innerHTML = `
            <div style="text-align: center; padding: 40px;">
                <h1 style="color: #dc3545; margin-bottom: 20px;">Error</h1>
                <p style="color: #666; margin-bottom: 30px;">${message}</p>
                <div class="form-footer">
                    <a href="/volunteering">Back to Opportunities</a> | 
                    <a href="/">Back to Home</a>
                </div>
            </div>
        `;
    }
}); 