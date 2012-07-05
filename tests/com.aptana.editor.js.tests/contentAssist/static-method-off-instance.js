function Podcast() {};

Podcast.FILE_EXTENSION = 'mp3';
Podcast.download = function(podcast) {
    console.log('Downloading ' + podcast + ' ...');
};

Podcast.prototype.play = function() {
    console.log('Playing this podcast ...');
};

Podcast.