const webpackMerge = require('webpack-merge');
const commonConfig = require('./webpack.common.js');
const helpers = require('./helpers');
const webpack = require('webpack');
const ngtools = require('@ngtools/webpack');
const path = require('path');


module.exports = webpackMerge(commonConfig, {
    devtool: 'cheap-module-eval-source-map',

    output: {
        path: helpers.root('dist'),
        publicPath: 'http://localhost:8080/',
        // publicPath: 'http://localhost:63342/confitura-page/dist/',
        filename: '[name].js',
        chunkFilename: '[id].chunk.js'
    },
    module: {
        rules: [
            {
                test: /\.ts$/,
                use: ['@ngtools/webpack']
            },
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
        }),
        new ngtools.AotPlugin({
            tsConfigPath: path.join(process.cwd(), 'tsconfig.json')
        })

    ],
    devServer: {
        historyApiFallback: true,
        https: false,
        stats: 'minimal',
        proxy: {
            "/resources/**": {
                target: "http://localhost:9090/",
                secure: false
            }
        }
    }
});
