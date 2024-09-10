// 로그아웃 버튼 클릭 시 실행되는 코드
const logoutButton = document.getElementById('logout-button');
if (logoutButton) {
    logoutButton.addEventListener('click', async (event) => {
        event.preventDefault(); // 기본 링크 동작 방지
        console.log("로그아웃 버튼 눌림!");


        // 서버에 로그아웃 요청 전송
        try {
            const accessToken = localStorage.getItem('jwtToken');
            const response = await fetch('/logout', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                    'Authorization': 'Bearer ' + accessToken
                }
            });

            if (response.ok) {
                // 로컬 스토리지에서 토큰 삭제
                localStorage.removeItem('jwtToken');
                showSuccess('로그아웃 되었습니다.', '/login');

                // alert("로그아웃 되었습니다.");
                // window.location.href = '/login'; // 로그아웃 후 리다이렉트할 페이지 설정
            } else {
                const errorMessage = await response.text();
                showError(errorMessage);
            }
        } catch (error) {
            console.error('로그아웃 중 오류 발생:', error);
            showError('로그아웃 중 오류가 발생했습니다.');
        }
    });
} else {
    console.error('로그아웃 버튼을 찾을 수 없습니다.');
}

function showSuccess(message, redirectUrl) {
    Swal.fire({
        icon: 'success',
        title: '성공',
        text: message,
        confirmButtonText: '확인'
    }).then((result) => {
        if (result.isConfirmed) {
            // 확인 버튼이 눌리면 페이지 이동
            window.location.href = redirectUrl;
        }
    });
}

function showError(message) {
    Swal.fire({
        icon: 'error',
        title: '입력 오류',
        text: message,
        confirmButtonText: '확인'
    });
}