document.addEventListener('DOMContentLoaded', function() {
    const form = document.getElementById('opportunity-form');
    const successMessage = document.getElementById('success-message');
    const errorMessage = document.getElementById('error-message');
    const errorText = document.getElementById('error-text');

    // Set minimum date to today
    const dateInput = document.getElementById('date');
    const today = new Date().toISOString().split('T')[0];
    dateInput.min = today;

    form.addEventListener('submit', function(e) {
        e.preventDefault();
        
        if (validateForm()) {
            submitForm();
        }
    });

    function validateForm() {
        const title = document.getElementById('title').value.trim();
        const description = document.getElementById('description').value.trim();
        const location = document.getElementById('location').value.trim();
        const type = document.getElementById('type').value;
        const date = document.getElementById('date').value;
        const status = document.getElementById('status').value;

        // Clear previous error
        hideError();

        // Validate required fields
        if (!title) {
            showError('Title is required');
            return false;
        }

        if (!description) {
            showError('Description is required');
            return false;
        }

        if (!location) {
            showError('Location is required');
            return false;
        }

        if (!type) {
            showError('Please select a type');
            return false;
        }

        if (!date) {
            showError('Date is required');
            return false;
        }

        if (!status) {
            showError('Please select a status');
            return false;
        }

        // Validate title length
        if (title.length > 100) {
            showError('Title must be 100 characters or less');
            return false;
        }

        // Validate description length
        if (description.length > 2000) {
            showError('Description must be 2000 characters or less');
            return false;
        }

        // Validate location length
        if (location.length > 100) {
            showError('Location must be 100 characters or less');
            return false;
        }

        return true;
    }

    function submitForm() {
        const formData = {
            title: document.getElementById('title').value.trim(),
            description: document.getElementById('description').value.trim(),
            location: document.getElementById('location').value.trim(),
            type: document.getElementById('type').value,
            date: document.getElementById('date').value,
            status: document.getElementById('status').value
        };

        // Disable form during submission
        const submitBtn = form.querySelector('button[type="submit"]');
        const originalText = submitBtn.textContent;
        submitBtn.disabled = true;
        submitBtn.textContent = 'Posting...';

        fetch('/api/volunteer/opportunities', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify(formData)
        })
        .then(response => {
            if (!response.ok) {
                return response.text().then(text => {
                    throw new Error(text || 'Failed to create opportunity');
                });
            }
            return response.json();
        })
        .then(data => {
            showSuccess();
            // Redirect after 2 seconds
            setTimeout(() => {
                window.location.href = '/volunteering';
            }, 2000);
        })
        .catch(error => {
            console.error('Error creating opportunity:', error);
            showError(error.message || 'Failed to create opportunity. Please try again.');
            
            // Re-enable form
            submitBtn.disabled = false;
            submitBtn.textContent = originalText;
        });
    }

    function showSuccess() {
        successMessage.style.display = 'block';
        errorMessage.style.display = 'none';
        form.style.display = 'none';
    }

    function showError(message) {
        errorText.textContent = message;
        errorMessage.style.display = 'block';
        successMessage.style.display = 'none';
        
        // Scroll to error message
        errorMessage.scrollIntoView({ behavior: 'smooth', block: 'center' });
    }

    function hideError() {
        errorMessage.style.display = 'none';
    }

    // Add real-time validation feedback
    const inputs = form.querySelectorAll('input, textarea, select');
    inputs.forEach(input => {
        input.addEventListener('blur', function() {
            validateField(input);
        });
    });

    function validateField(field) {
        const value = field.value.trim();
        const fieldName = field.name || field.id;
        
        // Remove existing error styling
        field.style.borderColor = '';
        
        // Check if field is required and empty
        if (field.hasAttribute('required') && !value) {
            field.style.borderColor = '#dc3545';
            return false;
        }
        
        // Field-specific validation
        switch (fieldName) {
            case 'title':
                if (value && value.length > 100) {
                    field.style.borderColor = '#dc3545';
                    return false;
                }
                break;
            case 'description':
                if (value && value.length > 2000) {
                    field.style.borderColor = '#dc3545';
                    return false;
                }
                break;
            case 'location':
                if (value && value.length > 100) {
                    field.style.borderColor = '#dc3545';
                    return false;
                }
                break;
        }
        
        return true;
    }
}); 