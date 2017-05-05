/**
 * Created by stephane on 05/05/2017.
 */

const Dashboard = React.createClass({

    getInitialState: function () {
        return {
            pokemons: ["pikachu"],
            pokemonName: "",
            pokeData: null
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
        $.get("http://localhost:9000/pokedex?name=" + this.state.pokemonName, function(data,status,xhr) {
           if(xhr.status == 200 && data != null) {
               that.setState({pokeData: data});
           }
        });
    },

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
                        elm(Pokemon, {pokeData: this.state.pokeData}, null)
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