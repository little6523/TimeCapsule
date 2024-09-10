let isFormDirty = false;

const form = document.querySelector('form');
form.addEventListener('input', function () {
    isFormDirty = true;
});

window.addEventListener('beforeunload', function (e) {
    if (isFormDirty) {
        e.preventDefault();
        e.returnValue = '';
    }
});

form.addEventListener('submit', function () {
    isFormDirty = false;
});