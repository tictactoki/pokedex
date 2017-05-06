/**
 * Created by wong on 01/05/17.
 */
const elm = React.createElement;

var Type = function (name, url) {
    this.name = name;
    this.url = url;
};

const Pokemon = React.createClass({

    getInitialState: function () {
        return {
            types: null,
            average_stats: []
        };
    },

    updateAverageStats: function () {
        var types = this.props.pokeData.types.map(function (obj) {
            new Type(obj.type.name, obj.type.url);
        });
        types.map(function (type) {
            this.getAverageStat(type)
        });
    },

    getAverageStat: function (type) {
        var that = this;
        $.get("http://localhost:9000/type?=" + type.name, function (data, status, xhr) {
            if (xhr.status == 200 && data != null) {
                var average = that.state.average_stats;
                average[type.name] = data;
                that.setState({average_stats: average});
            }
        });
    },

    componentDidMount: function () {
        //this.updateAverageStats();
    },

    render: function () {
        if (this.props.pokeData != null) {
            console.log(this.props.pokeData);
            return (
                elm("table", null,
                    elm("tbody", null,
                        elm("tr", null, this.props.pokeData.stats.map(function (data) {
                                return elm("th", null, data.stat.name);
                            })
                        ),
                        elm("tr", null, this.props.pokeData.stats.map(function(data) {
                                return elm("td", null, data.base_stat);
                            })
                        )
                    )
                )
            );
        }
        else {
            return (
                elm("div", null, null)
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
/*ReactDOM.render(
 elm(Pokemon, null,null),
 document.getElementById("container")
 );*/