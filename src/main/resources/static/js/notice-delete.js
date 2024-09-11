function deleteNotice(id) {
    if (confirm('정말 삭제하시겠습니까?')) {
        fetch(`/notices/${id}`, {
            method: 'DELETE',
            headers: {
                'Content-Type': 'application/json'
            },
        })
            .then(response => {
                if (response.ok) {
                    alert('삭제되었습니다.');
                    window.location.href = '/notices';
                } else {
                    alert('삭제 중 오류가 발생했습니다.');
                }
            })
            .catch(error => {
                console.error('Error:', error);
                alert('삭제 중 오류가 발생했습니다.');
            });
    }
}