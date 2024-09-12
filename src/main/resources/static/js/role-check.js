document.addEventListener("DOMContentLoaded", function () {

    const userRole = document.getElementById('userRole').value;

    if (userRole === 'ADMIN') {
        console.log('관리자 권한이 확인되었습니다.');

        document.getElementById('adminButton').style.display = 'block';
        document.getElementById('adminActions').style.display = 'block';

    } else {
        console.log('일반 사용자입니다.');

        document.getElementById('adminButton').style.display = 'none';
        document.getElementById('adminActions').style.display = 'none';
    }
});