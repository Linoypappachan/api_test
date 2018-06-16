const ENV = process.env.npm_lifecycle_event,
    _prod = (ENV === 'build') ? true : false,
    path = require('path'),
    webpack = require('webpack'),
    ExtractTextPlugin = require("extract-text-webpack-plugin"),
    HtmlWebpackPlugin = require('html-webpack-plugin'),
    CopyWebpackPlugin = require('copy-webpack-plugin'),
    ProvidePlugin = require('webpack/lib/ProvidePlugin'),
    CommonsChunkPlugin = require('webpack/lib/optimize/CommonsChunkPlugin'),
    devServer = {
        stats: 'minimal',
        host: 'localhost',
        port: 8888
    }
;

let config = {
    entry: {
        'app' : './src/app/app.js'
    },
    devtool : _prod? 'false': 'source-map',
    output: {
        publicPath:'web_testportal',
        path: path.resolve('public'),
        filename: 'js/[name].js',
    },

    module: {
        loaders: [
            {
                test: /\.(scss|css)$/,
                    use: ExtractTextPlugin.extract({
                    fallback: "style-loader",
                    use: ['css-loader', 'postcss-loader', 'sass-loader'],
                    allChunks: true
                })                
            },
            {
                test: /\.(svg|woff|woff2|ttf|eot|otf|png)$/,
                use: ['url-loader']
            },
            {
                test: /\.html$/,
                loader: 'raw-loader'
            }
        ]
    },

    plugins: [
            new ExtractTextPlugin({filename: 'css/style.css'}),
            new HtmlWebpackPlugin({
                filename: 'index.html',
                template: './src/index.html',
                inject: 'body'
            }),
            new ProvidePlugin({
                $: "jquery",
                jQuery: "jquery",
                'window.jQuery': "jquery",
                moment: "moment",
            }),
            new webpack.DefinePlugin({
                PRODUCTION: (_prod) ? JSON.stringify(true) : JSON.stringify(false),
                rest_url_prefix: (!_prod) ? JSON.stringify('http://localhost:9000/rs') 
                    : JSON.stringify('/svc_testportal/rs')
            }),
            new CopyWebpackPlugin([
                {
                    from: path.resolve(__dirname, 'src', 'app', 'images'), 
                    to: path.resolve('public', 'images')
                },
                {
                    from: path.resolve(__dirname, 'src', 'app', 'partials'),
                    to: path.resolve('public', 'partials')
                }
            ])
    ],

    devServer
};

module.exports = config;