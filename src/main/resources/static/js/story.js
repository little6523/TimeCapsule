const eventSource = new EventSource('http://localhost:8080/api/story');
const story = document.getElementById('content')

eventSource.onmessage = function (event) {
    try {
        let data = event.data.replace(/'/g, '"');
        console.log(data);
        const jsonData = JSON.parse(data);

        if (jsonData.type === "complete") {
            eventSource.close();
        } else {
            const parsedContent = jsonData.data.content;
            if (parsedContent !== undefined) {
                story.innerText += parsedContent;
            }
        }
    } catch (error) {
        console.error('Error processing event data:', error);
    }
};

eventSource.onerror = function (error) {
    console.error('SSE error:', error);
    eventSource.close();
};