/**
 * Created by stephane on 05/05/2017.
 */

const Dashboard = React.createClass({

    getInitialState: function () {
        return {
            pokemons: [],
            pokemonName: "",
            pokeData: null,
            average_stats: [],
            types: [],
        };
    },

    autocompleteName: function (event) {
        event.preventDefault();
        var value = event.target.value;
        var that = this;
        $.get("http://localhost:9000/autocomplete?prefix=" + value, function (data, status, xhr) {
            if (xhr.status == 200 && data != null) {
                that.setState({pokemons: data, pokemonName: value});
            }
        });
    },

    pokedex: function (event) {
        event.preventDefault();
        var that = this;
        //console.log(this.state.pokemonName);
        $.get("http://localhost:9000/pokedex?name=" + this.state.pokemonName, function (data, status, xhr) {
            if (xhr.status == 200 && data != null) {
                that.setState({pokeData: data, average_stats: [], types: []});
                that.updateAverageStats();
            }
        });
    },

    updateAverageStats: function () {
        //console.log(this.props.pokeData.types);
        var that = this;
        var types = this.state.pokeData.types.map(function (obj) {
            return new Type(obj.type.name, obj.type.url);
        });
        console.log(types);
        types.map(function (type) {
            that.getAverageStat(type)
        });
    },

    getAverageStat: function (type) {
        var that = this;
        console.log("average");
        $.get("http://localhost:9000/pokemonType?name=" + type.name + "&url=" + type.url, function (data, status, xhr) {
            if (xhr.status == 200 && data != null) {
                var average = that.state.average_stats;
                var types = that.state.types;
                types.push(type.name);
                average[type.name] = data;
                that.setState({average_stats: average, types: types});
                console.log(that.state.average_stats);
            }
            else {
                console.log("nok");
            }
        });
    },

    /*componentDidMount: function () {
        if (this.state.pokeData != null) {
            this.updateAverageStats();
        }
    },*/

    render: function () {
        if (this.state.pokemons != null) {
            return (
                elm("div", {className: "dashboard"},
                    elm("form", {onSubmit: this.pokedex},
                        elm("div", {className: "search"},
                            elm("input", {
                                onChange: this.autocompleteName,
                                value: this.state.pokemonName,
                                type: "text",
                                list: "pokemons"
                            }, null),
                            elm("datalist", {id: "pokemons"}, this.state.pokemons.map(function (pokemon) {
                                    return elm("option", {value: pokemon}, pokemon);
                                })
                            ),
                            elm("input", {type: "submit", value: "Search"})
                        )
                    ),
                    elm("div", {className: "pokedex"},
                        elm(Pokemon, {
                            pokeData: this.state.pokeData,
                            average_stats: this.state.average_stats,
                            types: this.state.types
                        }, null)
                    )
                )
            );
        }
        else {
            return (
                elm("div", {className: "dashboard"},
                    elm("input", {onChange: this.autocompleteName, type: "text", list: "pokemons"}, null),
                    elm("datalist", {id: "pokemons"}, null)
                )
            );
        }
    }
    //return elm("a",{className: "twitter-timeline",href: "https://twitter.com/hashtag/charizard", 'data-widget-id': "859164250522214400"},"tweet");

});
ReactDOM.render(
    elm(Dashboard, null, null),
    document.getElementById("container")
);