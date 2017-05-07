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
        };
    },

    createAverageElement: function(stat) {
        console.log(stat);
        return elm("tr", null,
            elm("td", null, stat.name),
            elm("td", null, stat.list["speed"]),
            elm("td", null, stat.list["special-defense"]),
            elm("td", null, stat.list["special-attack"]),
            elm("td", null, stat.list["defense"]),
            elm("td", null, stat.list["attack"]),
            elm("td", null, stat.list["hp"])
        );
    },

    render: function () {
        var that = this;
        if (this.props.pokeData != null) {
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
                                    elm("td", null, (this.props.pokeData.weight.toFixed(2) / 10.0).toFixed(2) + " kg")
                                ),
                                elm("tr", null,
                                    elm("td", null, "Height"),
                                    elm("td", null, (this.props.pokeData.height / 10.0).toFixed(2) + " m")
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
                    ),
                    elm("table", null,
                        elm("tbody", null,
                            elm("tr", null,
                                elm("th", null, "type"),
                                elm("th", null, "speed"),
                                elm("th", null, "special-defense"),
                                elm("th", null, "special-attack"),
                                elm("th", null, "defense"),
                                elm("th", null, "attack"),
                                elm("th", null, "hp")
                            ),
                            this.props.types.map(function(type) {
                                var stat = that.props.average_stats[type];
                                return elm("tr", null,
                                    elm("td", null, stat.name),
                                    elm("td", null, stat.list["speed"]),
                                    elm("td", null, stat.list["special-defense"]),
                                    elm("td", null, stat.list["special-attack"]),
                                    elm("td", null, stat.list["defense"]),
                                    elm("td", null, stat.list["attack"]),
                                    elm("td", null, stat.list["hp"])
                                );
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