const webpack = require('webpack');
const HtmlWebpackPlugin = require('html-webpack-plugin');
const ExtractTextPlugin = require("extract-text-webpack-plugin");
const helpers = require('./helpers');
const CleanWebpackPlugin = require('clean-webpack-plugin');


module.exports = {
    entry: {
        'polyfills': './src/polyfills.ts',
        'app': './src/main.ts'
    },

    resolve: {
        extensions: ['.js', '.ts'],
        alias: {
            jquery: 'jquery/src/jquery',
            select2: 'select2'
        }
    },

    module: {
        rules: [
            {
                test: /\.html$/,
                loader: 'html-loader',
                exclude: /node_modules/
            },
            {
                test: /\.html$/,
                loader: 'raw-loader',
                include: /node_modules/
            },
            {
                test: /\.(woff|woff2|ttf|eot|ico)$/,
                use: {
                    loader: 'file-loader',
                    options: {name: 'assets/[name].[hash].[ext]'}
                }
            },
            {
                test: /\.(ttf|eot|svg)(\?v=[0-9]\.[0-9]\.[0-9])?$/,
                use: {
                    loader: 'file-loader'
                }
            },
            {
                test: /\.css$/,
                exclude: helpers.root('src', 'app'),
                use: ExtractTextPlugin.extract({
                    fallback: "style-loader",
                    use: "css-loader"
                })
            },
            {
                test: /\.scss$/,
                exclude: helpers.root('src', 'app'),
                use: ["to-string-loader", "css-loader", "sass-loader"]
            },
            {
                test: /\.scss$/,
                include: helpers.root('src', 'app'),
                use: ExtractTextPlugin.extract({
                    fallback: "style-loader",
                    use: ["css-loader", "sass-loader"]
                })

            }
        ]
    },

    plugins: [
        new webpack.optimize.CommonsChunkPlugin({
            name: 'vendor',
            minChunks: function (m) {
                return m.context && m.context.includes('node_modules')
            }
        }),
        new webpack.optimize.CommonsChunkPlugin({
            name: 'manifest',
            // chunks: ['vendor'],
            // minChunks: Infinity
        }),
        new HtmlWebpackPlugin({
            template: 'src/index.html',
            favicon: 'src/app/img/favicon.ico',
            chunksSortMode: 'manual',
            chunks: ['manifest', 'polyfills', 'vendor', 'app']
        }),
        new ExtractTextPlugin("[name].[contenthash].css"),
        new CleanWebpackPlugin(['dist'], {
            root: helpers.root()
        }),
        new webpack.HashedModuleIdsPlugin(),
    ]
}
;
