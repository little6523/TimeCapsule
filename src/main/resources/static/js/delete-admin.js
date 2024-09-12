function deleteNotice(noticeId) {
    Swal.fire({
        title: '정말로 삭제하시겠습니까?',
        text: "삭제한 내용은 복구할 수 없습니다.",
        icon: 'warning',
        showCancelButton: true,
        confirmButtonColor: '#DBBABA',
        cancelButtonColor: '#EFDDDD',
        confirmButtonText: '삭제',
        cancelButtonText: '취소',
        reverseButtons: true
    }).then((result) => {
        if (result.isConfirmed) {
            fetch(`/notices/${noticeId}`, {
                method: 'DELETE',
                headers: {
                    'Content-Type': 'application/json'
                }
            }).then(response => {
                if (response.ok) {
                    Swal.fire({
                        title: '삭제 완료',
                        text: '공지사항이 성공적으로 삭제되었습니다.',
                        icon: 'success',
                        confirmButtonColor: '#DBBABA'
                    }).then(() => {
                        window.location.href = '/notices';
                    });
                } else {
                    Swal.fire(
                        '삭제 실패',
                        '문제가 발생했습니다. 다시 시도해 주세요.',
                        'error'
                    );
                }
            }).catch(error => {
                console.error('Error:', error);
                Swal.fire(
                    '삭제 실패',
                    '문제가 발생했습니다. 다시 시도해 주세요.',
                    'error'
                );
            });
        }
    });
}