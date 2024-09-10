// document.addEventListener('DOMContentLoaded', () => {
//     const token = localStorage.getItem('jwtToken');
//     if (token) {
//         alert("이미 로그인 되어있습니다!");
//         window.location.href = '/main';
//         return;
//     }
// });

document.getElementById('loginForm').addEventListener('submit', async function (event) {
    event.preventDefault(); // 폼의 기본 제출 동작을 막음
    const token = localStorage.getItem('jwtToken');
    if (token) {
        alert("이미 로그인 되어있습니다!");
        window.location.href = '/main';
        return;
    }
// 폼 데이터 수집
    const userId = document.getElementById('username').value;
    const password = document.getElementById('password').value;

    console.log(userId, password);
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
                localStorage.setItem('jwtToken', token.replace('Bearer ', ''));
                console.log(localStorage.getItem('jwtToken'));
                // 메인 페이지로 리다이렉트
                window.location.href = '/main';

            } else {
                throw new Error('토큰을 찾을 수 없습니다.');
            }
        } else {
            // 로그인 실패 처리
            document.getElementById('error-message').style.display = 'block';
        }
    } catch (error) {
        console.error('로그인 요청 중 오류 발생:', error);
        document.getElementById('error-message').style.display = 'block';
    }
});