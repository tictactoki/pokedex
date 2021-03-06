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
            bookmarked: false,
        };
    },

    CheckUnCheckButton: function (event) {
        event.preventDefault();
        if (this.props.bookmarked) {
            this.props.unCheckButton(event, this.props.pokeData.name);
        }
        else {
            this.props.checkButton(event, this.props.pokeData.name);
        }
    },

    createBookmarkedButton: function () {
        var button = null;
        console.log(this.props.bookmarked);
        if (this.props.bookmarked) {
            button = elm("input", {
                type: "checkbox",
                value: this.props.pokeData.name,
                onClick: this.unCheckButton,
                checked: true
            }, null);
        }
        else {
            button = elm("input", {
                type: "checkbox",
                value: this.props.pokeData.name,
                onClick: this.checkButton,
                checked: false
            }, null);
        }
        return button;
    },

    render: function () {
        var that = this;
        if (this.props.pokeData != null) {
            return (
                elm("div", null,
                    elm("div", null,
                        elm("table", {className: "table table-striped"},
                            elm("tbody", null,
                                elm("tr", null,
                                    elm("th", {colspan: "2"}, "Informations"),
                                    elm("th", null, "Values")
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
                                ),
                                elm("tr", null,
                                    elm("td", null, "Bookmark"),
                                    elm("td", null,
                                        button = elm("input", {
                                            type: "checkbox",
                                            value: this.props.pokeData.name,
                                            onClick: this.CheckUnCheckButton,
                                            checked: this.props.bookmarked
                                        }, null)
                                    )
                                )
                            )
                        ),
                        elm("table", {className: "table table-striped"},
                            elm("tbody", null,
                                elm("tr", null,
                                    elm("th", null, "Type")
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
                    elm("table", {className: "table table-striped"},
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
                    elm("table", {className: "table table-striped"},
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
                            this.props.types.map(function (type) {
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
                    ),
                    elm("table", {className: "table table-striped"},
                        elm("tbody", null,
                            elm("tr", null,
                                elm("th", null, "User"),
                                elm("th", null, "Message")
                            ),
                            this.props.tweets.map(function (tweet) {
                                return elm("tr", null,
                                    elm("td", null, tweet.user),
                                    elm("td", null, tweet.text)
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

});