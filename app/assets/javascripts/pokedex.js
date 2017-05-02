/**
 * Created by wong on 01/05/17.
 */
const elm = React.createElement;


const Pokemon = React.createClass({

    render: function () {
        return elm("a",{className: "twitter-timeline",href: "https://twitter.com/hashtag/charizard", 'data-widget-id': "859164250522214400"},"tweet");
    }

});

$(document).ready(function () {
    twttr.widgets.createTimeline(
        {
            sourceType: 'profile',
            screenName: 'charizard'
        },
        document.getElementById('content'),
        {
            width: '450',
            height: '400',
            related: 'charizard,twitterapi'
        }).then(function (el) {
        console.log('Embedded a timeline.')
    });
});
/*ReactDOM.render(
  elm(Pokemon, null,null),
  document.getElementById("content")
);*/