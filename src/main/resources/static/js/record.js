const recordButton = document.getElementById('record-button');
const fileUpload = document.getElementById("file-upload");

let constraints;
let chunks;
let mediaRecorder;
let dataTransfer = new DataTransfer();

if (navigator.mediaDevices) {

    constraints = {audio: true, video: false};
    chunks = [];

    navigator.mediaDevices.getUserMedia(constraints).then((stream) => {
        mediaRecorder = new MediaRecorder(stream);

        mediaRecorder.ondataavailable = (e) => {
            chunks.push(e.data);
        };

        mediaRecorder.onstop = () => {
            console.log("Recording has stopped.");
            const blob = new Blob(chunks, {type: 'audio/ogg; codecs=opus'});
            chunks = [];

            const file = new File([blob], "recording.ogg", {
                type: 'audio/ogg',
                lastModified: new Date().getTime()
            });

            dataTransfer.items.add(file);
            fileUpload.files = dataTransfer.files[Symbol.iterator];
            console.log(fileUpload.files);

            uploadText.innerText = dataTransfer.map(v => v.name).join("<br>");
            uploadContainer.classList.remove("active");
        }
    }).catch((err) => {
        console.error("The following error occurred: " + err);
    });
} else {
    console.error("getUserMedia is not supported in this browser.");
}

recordButton.addEventListener('click', function () {
    if (mediaRecorder.state !== 'recording') {
        this.style.backgroundImage = 'url(../images/AfterRecording.png)';
        document.getElementById('record-text').innerText = '녹음 중...'
        mediaRecorder.start();
        console.log(mediaRecorder.state);
        console.log("recorder started");
    } else {
        this.style.backgroundImage = 'url(../images/BeforeRecording.png)';
        document.getElementById('record-text').innerText = '음성 녹음을 시작하려면 버튼을 누르세요.'
        mediaRecorder.stop();
        console.log(mediaRecorder.state);
        console.log("recorder stopped");
    }
});
