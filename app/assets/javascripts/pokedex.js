/**
 * Created by wong on 01/05/17.
 */
const elm = React.createElement;


const Pokemon = React.createClass({

    getInitialState: function() {
      return {
          pokemonsName: []
      };
    },

    autocompleteName: function(event) {
        event.preventDefault();
        var value = event.target.value;
        var that = this;
        $.get("http://localhost:9000/autocomplete?prefix=" + value, function(data,status,xhr) {
           if(xhr.status == 200 && data != null) {
               that.setState({pokemonsName: data});
           }
        });
    },

    shouldComponentUpdate: function (nextProps) {
      return true;
    },

    render: function () {
        if (this.state.poekmonsName != null) {
            console.log("test");
            return (
                elm("div", null,
                    elm("input", {onChange: this.autocompleteName, type: "text", list: "pokemonsName"}, null),
                    elm("datalist", {id: "pokemonsName"}, this.state.pokemonsName.map(function (pokemon) {
                            return elm("option", {value: pokemon}, pokemon);
                        })
                    )
                )
            );
        }
        else {
            return (
                elm("div", null,
                    elm("input", {onChange: this.autocompleteName, type: "text", list: "pokemonsName"}, null),
                    elm("datalist", {id: "pokemonsName"}, null)
                )
            );
        }
    }
        //return elm("a",{className: "twitter-timeline",href: "https://twitter.com/hashtag/charizard", 'data-widget-id': "859164250522214400"},"tweet");

});

/*$(document).ready(function () {
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
});*/
ReactDOM.render(
  elm(Pokemon, null,null),
  document.getElementById("content")
);