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
                elm("div", null,
                    elm("div", null,
                        elm("table", null,
                            elm("tbody", null,
                                elm("tr", null,
                                    elm("th", {colspan: "2"}, "Informations")
                                ),
                                elm("tr", null,
                                    elm("td", null, "Image"),
                                    elm("img", {src: this.props.pokeData.sprites["front_default"]}, null)
                                ),
                                elm("tr", null,
                                    elm("td", null, "Name"),
                                    elm("td", null, this.props.pokeData.name)
                                ),
                                elm("tr", null,
                                    elm("td", null, "Weight"),
                                    elm("td", null, (this.props.pokeData.weight / 10.0) + " kg")
                                ),
                                elm("tr", null,
                                    elm("td", null, "Height"),
                                    elm("td", null, (this.props.pokeData.height / 10.0) + " m")
                                )
                            )
                        ),
                        elm("table", null,
                            elm("tbody", null,
                                elm("tr", null,
                                    elm("th", null, "Types")
                                ),
                                elm("tr", null,
                                    elm("td", {rowspan: (this.props.pokeData.types.length + "")},
                                        this.props.pokeData.types.map(function (data) {
                                            return elm("p", null, data.type.name);
                                        })
                                    )
                                )
                            )
                        )
                    ),
                    elm("table", null,
                        elm("tbody", null,
                            elm("tr", null, this.props.pokeData.stats.map(function (data) {
                                    return elm("th", null, data.stat.name);
                                })
                            ),
                            elm("tr", null, this.props.pokeData.stats.map(function (data) {
                                    return elm("td", null, data.base_stat);
                                })
                            )
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