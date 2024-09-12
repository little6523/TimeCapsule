document.addEventListener('DOMContentLoaded', function() {
    const commentForm = document.querySelector('.comment-form');
    const commentListContainer = document.getElementById('commentListContainer');
    const storyId = window.storyId;

    if (!storyId) {
        console.error('Story ID not found');
        return;
    }

    loadComments(storyId);

    if (commentForm) {
        commentForm.addEventListener('submit', function(e) {
            e.preventDefault();
            const content = this.querySelector('textarea[name="content"]').value.trim();
            if (content === '') {
                alert('댓글 내용을 입력해주세요.');
                return;
            }
            submitComment(this, storyId, content);
        });
    } else {
        console.error('Comment form not found');
    }

    if (commentListContainer) {
        commentListContainer.addEventListener('click', function(e) {
            // 댓글 삭제
            if (e.target.classList.contains('delete-comment')) {
                if (confirm('정말로 이 댓글을 삭제하시겠습니까?')) {
                    deleteComment(e.target.closest('.comment-item').dataset.id, storyId);
                }
            }
            // 댓글 수정 모드 활성화/비활성화
            else if (e.target.classList.contains('edit-comment') || e.target.classList.contains('cancel-edit')) {
                toggleEditMode(e.target.closest('.comment-item'));
            }
            // 수정 완료 버튼 클릭 시
            else if (e.target.classList.contains('save-edit')) {
                const commentItem = e.target.closest('.comment-item');
                const newContent = commentItem.querySelector('textarea').value.trim();
                if (newContent === '') {
                    alert('댓글 내용을 입력해주세요.');
                    return;
                }
                updateCommentOnServer(commentItem.dataset.id, newContent, storyId);
            }
        });
    } else {
        console.error('Comment list container not found');
    }
});

function loadComments(storyId) {
    fetch(`/comments/story/${storyId}`)
        .then(response => {
            if (!response.ok) throw new Error('댓글을 불러오는데 실패했습니다.');
            return response.json();
        })
        .then(comments => {
            const commentListContainer = document.getElementById('commentListContainer');
            if (commentListContainer) {
                commentListContainer.innerHTML = `<div class="commentList">` + comments.map(comment => `
                    <div class="comment-item" data-id="${comment.id}">
                        <div class="comment-header">
                            <span class="comment-author">${comment.userNickname} | ${new Date(comment.createdAt).toLocaleString()}</span>
                        </div>
                        <div class="comment-content">${comment.content}</div>
                        <div class="comment-edit-form" style="display: none;">
                            <textarea>${comment.content}</textarea>
                            <button class="save-edit" type="button">수정 완료</button>
                            <button type="button" class="cancel-edit">취소</button>
                        </div>
                        <div class="commentListButtons">
                            <button class="edit-comment">수정</button>
                            <button class="delete-comment">삭제</button>
                        </div>
                    </div>
                `).join('') + `</div>`;
            } else {
                console.error('Comment list container not found');
            }
        })
        .catch(error => {
            console.error('Error loading comments:', error);
            alert(error.message);
        });
}

function submitComment(form, storyId, content) {
    fetch('/comments', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({
            storyId: storyId,
            content: content,
            userId: document.getElementById('userId').value
        })
    })
        .then(response => {
            if (!response.ok) throw new Error('댓글 작성에 실패했어요.');
            return response.json();
        })
        .then(data => {
            console.log('댓글이 성공적으로 작성되었습니다:', data);
            form.reset();
            loadComments(storyId);
        })
        .catch(error => {
            console.error('Error:', error);
            alert(error.message);
        });
}

function updateCommentOnServer(commentId, newContent, storyId) {
    fetch(`/comments/${commentId}`, {
        method: 'PUT',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({
            content: newContent,
            userId: document.getElementById('userId').value
        })
    })
        .then(response => {
            if (!response.ok) throw new Error('댓글 수정에 실패했습니다.');
            return response.json();
        })
        .then(data => {
            console.log('댓글이 성공적으로 수정되었습니다:', data);
            loadComments(storyId);
        })
        .catch(error => {
            console.error('Error:', error);
            alert(error.message);
        });
}

function deleteComment(commentId, storyId) {
    fetch(`/comments/${commentId}`, {
        method: 'DELETE',
        headers: {
            'Content-Type': 'application/json',
            'User-Id': document.getElementById('userId').value
        }
    })
        .then(response => {
            if (!response.ok) throw new Error('댓글 삭제에 실패했습니다.');
            return response.text();
        })
        .then(() => {
            console.log('댓글이 성공적으로 삭제되었습니다.');
            loadComments(storyId);
        })
        .catch(error => {
            console.error('Error:', error);
            alert(error.message);
        });
}


function toggleEditMode(commentItem) {
    const contentElement = commentItem.querySelector('.comment-content');
    const editForm = commentItem.querySelector('.comment-edit-form');
    const buttonsElement = commentItem.querySelector('.commentListButtons');
    const cancelButton = commentItem.querySelector('.cancel-edit');

    if (contentElement.style.display !== 'none') {
        contentElement.style.display = 'none';
        buttonsElement.style.display = 'none';
        editForm.style.display = 'block';
        cancelButton.style.display = 'inline'; // Show cancel button when editing
    } else {
        contentElement.style.display = 'block';
        buttonsElement.style.display = 'block';
        editForm.style.display = 'none';
        cancelButton.style.display = 'none';
    }
}
