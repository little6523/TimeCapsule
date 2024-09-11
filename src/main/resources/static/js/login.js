document.addEventListener('DOMContentLoaded', () => {
    const token = sessionStorage.getItem('jwtToken');
    if (token) {
        showError("이미 로그인 되어있습니다!");

    }
});

document.getElementById('loginForm').addEventListener('submit', async function (event) {
    event.preventDefault(); // 폼의 기본 제출 동작을 막음
    const token = sessionStorage.getItem('jwtToken');

    if (token) {
        showError("이미 로그인 되어있습니다!");

    }
// 폼 데이터 수집
    const userId = document.getElementById('username').value;
    const password = document.getElementById('password').value;

    try {
// fetch를 사용하여 로그인 요청
        const response = await fetch('/login', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({
                userId: userId,
                password: password
            })
        });

        if (response.ok) {
            // 응답 헤더에서 JWT 토큰 추출
            const token = response.headers.get('Authorization');
            if (token) {
                // 로컬 스토리지에 토큰 저장
                sessionStorage.setItem('jwtToken', token.replace('Bearer ', ''));
                // 메인 페이지로 리다이렉트
                window.location.href = '/main';

            } else {
                throw new Error('토큰을 찾을 수 없습니다.');
            }
        } else {
            // 아이디, 비밀번호 불일치
            const errorMessage = await response.text();
            showError(errorMessage);
        }
    } catch (error) {
        showError("로그인 요청 중 오류 발생");
        console.error('로그인 요청 중 오류 발생:', error);
    }
});

function showError(message) {
    Swal.fire({
        icon: 'error',
        title: '입력 오류',
        text: message,
        confirmButtonText: '확인'
    }).then((result) => {
        if (result.isConfirmed) {
            // history.back();
        }
    })
}
  // 페이지 로드 시 showError 함수 실행