/**
 * Created by stephane on 05/05/2017.
 */

const Dashboard = React.createClass({

    getInitialState: function () {
        return {
            pokemons: [],
            pokemonName: null,
            pokeData: null,
            average_stats: [],
            types: [],
            bookmarks: [],
            bookmarked: false,
            tweets: [],
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

    checkPokemonBookmarked: function (name) {
        var index = this.state.bookmarks.indexOf(name);
        return (index >= 0);
    },

    pokedex: function (event) {
        event.preventDefault();
        this.getPokemon(this.state.pokemonName);
    },

    getPokemon: function (name) {
        var that = this;
        $.get("http://localhost:9000/pokedex?name=" + name, function (data, status, xhr) {
            if (xhr.status == 200 && data != null) {
                var bookmarked = that.checkPokemonBookmarked(name);
                that.setState({pokeData: data, average_stats: [], types: [], bookmarked: bookmarked});
                that.updateAverageStats();
                that.getTweets(name);
            }
        });
    },

    getTweets: function(name) {
        var that = this;
        $.get("http://localhost:9000/getTweets?name=" + name, function (data, status, xhr) {
           if(xhr.status == 200 && data != null) {
               that.setState({tweets: data});
           }
        });
    },

    updateAverageStats: function () {
        var that = this;
        var types = this.state.pokeData.types.map(function (obj) {
            return new Type(obj.type.name, obj.type.url);
        });
        types.map(function (type) {
            that.getAverageStat(type)
        });
    },

    getAverageStat: function (type) {
        var that = this;
        $.get("http://localhost:9000/pokemonType?name=" + type.name + "&url=" + type.url, function (data, status, xhr) {
            if (xhr.status == 200 && data != null) {
                var average = that.state.average_stats;
                var types = that.state.types;
                types.push(type.name);
                average[type.name] = data;
                that.setState({average_stats: average, types: types});
            }
            else {
                console.log("KO on data type");
            }
        });
    },

    getBookmarks: function () {
        var that = this;
        $.get("http://localhost:9000/bookmarks", function (data, status, xhr) {
            if (xhr.status == 200 && data != null) {
                that.setState({bookmarks: data});
            }
        });
    },

    bookmarkPokemon: function (event, name) {
        event.preventDefault();
        var that = this;
        $.get("http://localhost:9000/bookmarkPokemon?name=" + name, function (data, status, xhr) {
            if (xhr.status == 200) {
                var bookmarks = that.state.bookmarks;
                bookmarks.push(name);
                that.setState({bookmarks: bookmarks, bookmarked: true});
            }
        });
    },

    removeBookmark: function (event, name) {
        event.preventDefault();
        var that = this;
        $.get("http://localhost:9000/removeBookmark?name=" + name, function (data, status, xhr) {
            if (xhr.status == 200) {
                var bookmarks = that.state.bookmarks;
                var index = bookmarks.indexOf(name);
                bookmarks.splice(index, 1);
                that.setState({bookmarks: bookmarks, bookmarked: false});
            }
        });
    },

    selectPokemon: function (event) {
        event.preventDefault();
        var value = event.target.value;
        this.getPokemon(value);
    },

    createBookmarkSelect: function () {
        var element = null;
        if (this.state.bookmarks.length > 0) {
            element = elm("select", {id: "bookmarks", onChange: this.selectPokemon, className: "form-control"},
                this.state.bookmarks.map(function (name) {
                    return elm("option", {value: name}, name);
                })
            );
        }
        else {
            element = elm("div", null, null);
        }
        return element;
    },

    logout: function (event) {
        event.preventDefault();
        $.get("http://localhost:9000/logout", function (data, status, xhr) {
            window.location.replace("http://localhost:9000/");
        });
    },

    componentDidMount: function () {
        this.getBookmarks();
    },

    render: function () {
        var select = this.createBookmarkSelect();
        if (this.state.pokemons != null) {
            return (
                elm("div", {className: "dashboard"},
                    elm("nav", {className: "navbar navbar-default"},
                        elm("form", {onSubmit: this.pokedex, className: "navbar-form"},
                            elm("div", {className: "form-group"},
                                elm("input", {
                                    onChange: this.autocompleteName,
                                    value: this.state.pokemonName,
                                    type: "text",
                                    list: "pokemons",
                                    className: "form-control"
                                }, null),
                                elm("datalist", {id: "pokemons"}, this.state.pokemons.map(function (pokemon) {
                                        return elm("option", {value: pokemon}, pokemon);
                                    })
                                ),
                                elm("button", {type: "submit", className: "btn btn-primary form-control"},
                                    elm("i", {className: "glyphicon glyphicon-search"}, null)
                                ),
                                select
                            ),
                            elm("ul", {className: "nav navbar-nav navbar-right"},
                                elm("button", {
                                    type: "button",
                                    value: "Logout",
                                    onClick: this.logout,
                                    className: "btn btn-primary form-control"
                                }, "Logout")
                            )
                        )
                    ),
                    elm("div", {className: "pokedex"},
                        elm(Pokemon, {
                            pokeData: this.state.pokeData,
                            average_stats: this.state.average_stats,
                            types: this.state.types,
                            bookmarks: this.state.bookmarks,
                            checkButton: this.bookmarkPokemon,
                            unCheckButton: this.removeBookmark,
                            bookmarked: this.state.bookmarked,
                            tweets: this.state.tweets
                        }, null)
                    ),
                    elm("div", {id: "timeline"}, null)
                )
            );
        }
        else {
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
                    elm("div", null, select),
                    elm("div", null,
                        elm("input", {type: "submit", value: "Logout", onClick: this.logout})
                    )
                )
            );
        }
    }

});
ReactDOM.render(
    elm(Dashboard, null, null),
    document.getElementById("container")
);