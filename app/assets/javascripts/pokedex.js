/**
 * Created by wong on 01/05/17.
 */
const elm = React.createElement;


const Pokemon = React.createClass({

    render: function () {
        return elm("a",{className: "twitter-timeline",href: "https://twitter.com/hashtag/pikachu", 'data-widget-id': "859164250522214400"},"tweet");
    }

});

ReactDOM.render(
  elm(Pokemon, null,null),
  document.getElementById("content")
);