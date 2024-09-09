const modalUtils = (() => {
    const showModal = (modal) => modal.style.display = 'flex';
    const hideModal = (modal) => modal.style.display = 'none';

    const showAlert = (message, callback) => {
        const alertModal = document.getElementById('alertModal');
        alertModal.querySelector('.alert-content h3').textContent = message;
        showModal(alertModal);

        const alertButton = alertModal.querySelector('.alert-button');
        const newAlertButton = alertButton.cloneNode(true); //alertButton 요소 복제하여 newAlertButton 요소에 저장
        alertButton.parentNode.replaceChild(newAlertButton, alertButton); //alertButton 요소를 newAlertButton 요소로 변경

        newAlertButton.addEventListener('click', () => {
            hideModal(alertModal);
            if (callback) callback(); //callback 이 함수인 경우 함수 실행
        });
    };

    //비밀번호 예외 처리 공통 함수
    const focusPasswordInput = () => {
        const confirmModal = document.getElementById('confirmModal');
        hideModal(confirmModal);
        document.getElementById('modalPassword').focus();
    };

    //비밀번호 예외 처리 공통 함수
    const handlePasswordError = (message) => {
        showAlert(message, focusPasswordInput);
    };

    return {
        showModal,
        hideModal,
        showAlert,
        focusPasswordInput,
        handlePasswordError
    };
})();

// 전역 객체에 modalUtils 할당
window.modalUtils = modalUtils;