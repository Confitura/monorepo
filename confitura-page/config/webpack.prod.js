var webpackMerge = require('webpack-merge');
var ExtractTextPlugin = require('extract-text-webpack-plugin');
var commonConfig = require('./webpack.common.js');
var helpers = require('./helpers');
var webpack = require('webpack');
var OptimizeCssAssetsPlugin = require('optimize-css-assets-webpack-plugin');
var ScriptExtHtmlWebpackPlugin = require('script-ext-html-webpack-plugin');


module.exports = webpackMerge(commonConfig, {

    output: {
        path: helpers.root('dist'),
        publicPath: 'http://2017.confitura.pl/',
        filename: '[name].[hash].js',
        chunkFilename: '[id].chunk.js'
    },
    module: {
        rules: [
            {
                test: /\.(gif|png|jpe?g|svg)$/i,
                use: [
                    {
                        loader: 'file-loader',
                        query: {name: 'assets/[name].[hash].[ext]'}

                    },
                    {
                        loader: 'image-webpack-loader',
                        query: {
                            progressive: true,
                            pngquant: {
                                quality: '65-90',
                                speed: 4
                            },
                            optipng: {
                                optimizationLevel: 7
                            },
                            gifsicle: {
                                interlaced: false
                            },
                            mozjpeg: {
                                quantTable: 1
                            }
                        }
                    }
                ]
            }
        ]
    },
    plugins: [
        new ExtractTextPlugin({
            filename: "[name].[contenthash].css",
            disable: false,
            allChunks: true
        }),

        new webpack.optimize.UglifyJsPlugin({
            beautify: false,
            mangle: {
                screw_ie8: true,
                keep_fnames: true
            },
            compress: {
                screw_ie8: true
            },
            comments: false
        }),
        new webpack.DefinePlugin({
            'API_URL': JSON.stringify("http://2017.confitura.pl/api/"),
            'ENV': JSON.stringify('prod')
        }),
        new OptimizeCssAssetsPlugin({
            cssProcessorOptions:{
                zindex: false
            }
        }),
        new ScriptExtHtmlWebpackPlugin({
            async: /app.+/,
            defaultAttribute: 'sync'
        })
        // new webpack.LoaderOptionsPlugin({
        //     minimize: true
        // })
    ]
});
