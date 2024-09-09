const API_URL = {
    RESET_PASSWORD : '/users/password/temporary',
    LOGIN_PAGE: '/login'
};

async function generateTemporaryPassword(event) {
    event.preventDefault(); // 기본 제출 방지

    const form = document.getElementById('findPasswordForm');
    let userId = document.getElementById('id').value.trim();
    let userEmail = document.getElementById('email').value.trim();

    //form 유효성 검사
    if(form.checkValidity()) {
        try {
            //비동기 통신
            let response = await fetch(API_URL.RESET_PASSWORD, {
                method: 'POST',
                headers: { 'Content-Type': 'application/json'},
                body: JSON.stringify({ id : userId, email : userEmail })
            });

            let responseBody = await response.json(); // 서버로부터 넘어 온 데이터를 JSON 형식으로 파싱

            if (!response.ok) {
                if(responseBody.success === false) {
                    modalUtils.showAlert(responseBody.message);
                } else {
                    throw new Error('임시 비밀번호 발급 처리 중 오류가 발생했습니다.');
                }
                return;
            }

            //데이터 로직 처리
            if(responseBody.success === true) {
                modalUtils.showAlert(responseBody.message, () => { window.location.href = API_URL.LOGIN_PAGE; });
            }

        } catch (error) {
            modalUtils.showAlert(error.message, () => { window.location.reload() });
        }
    } else {
        form.reportValidity();
    }
}