const express = require('express');
const path = require('path');

const app = express();
const port = 3000;

app.use(express.static(path.join(__dirname, "videos")));

app.listen(port, () => {
	console.log(`Server is running on port ${port}`);
	console.log(`Your videos are available at 'http://your-ip-adress:${port}/your-video-name.type`);
	console.log(`You can get your ip-adress using the command 'ipconfig' in your terminal`)
})