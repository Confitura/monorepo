var webpackMerge = require('webpack-merge');
var ExtractTextPlugin = require('extract-text-webpack-plugin');
var commonConfig = require('./webpack.common.js');
var helpers = require('./helpers');
var webpack = require('webpack');


module.exports = webpackMerge(commonConfig, {
    devtool: 'cheap-module-eval-source-map',

    output: {
        path: helpers.root('dist'),
        publicPath: 'http://localhost:8080/',
        // publicPath: 'http://localhost:63342/confitura-page/dist/',
        filename: '[name].js',
        chunkFilename: '[id].chunk.js'
    },
    module:{
        rules:[
            {
                test: /\.(png|jpe?g|gif|svg)$/,
                use: {
                    loader: 'file-loader',
                    options: {name: 'assets/[name].[ext]'}
                }
            },
        ]
    },

    plugins: [
        new webpack.DefinePlugin({
            'API_URL': JSON.stringify("http://localhost:9090"),
            'ENV': JSON.stringify('dev')
        })

    ],
    devServer: {
        historyApiFallback: true,
        stats: 'minimal',
        proxy:{
            "/resources/**": {
                target: "http://localhost:9090/",
                secure: false
            }
        }
    }
});
