// Modules
var path = require('path');
var webpack = require('webpack');
var AutoPrefixer = require('autoprefixer');
var PostCssFlexbugsFixes = require('postcss-flexbugs-fixes');
// Plugins
var HtmlWebpackPlugin = require('html-webpack-plugin');
var WebpackCleanupPlugin = require('webpack-cleanup-plugin');
var ManifestPlugin = require("webpack-manifest-plugin");
// Variables
var devServerHost = process.env.NODE_PUBLIC_HOST || 'localhost:3000';
var apiServerUrl = process.env.API_URL || 'http://localhost:8080';
var mode = process.env.NODE_ENV || 'development';

module.exports = {
    entry: {
        app: ['./src/app/index.tsx'],
        vendor: ['react', 'react-dom', 'react-router', 'redux', 'react-redux', 'redux-thunk']
    },
    output: {
        filename: 'main.bundle.js',
        path: path.resolve(__dirname, 'dist'),
        publicPath: '/'
    },
    resolve: {
        extensions: ['.js', '.jsx', '.json', '.ts', '.tsx'],
        modules: ['node_modules']
    },
    module: {
        rules: [
            {
                test: /\.(ts|tsx)$/,
                loader: 'ts-loader'
            },
            {
                enforce: 'pre',
                test: /\.js$/,
                loader: 'source-map-loader'
            },
            {
                test: /\.css$/,
                use: [
                    'style-loader',
                    {
                        loader: 'css-loader',
                        options: {
                            importLoaders: 1
                        }
                    },
                    {
                        loader: 'postcss-loader',
                        options: {
                            // Necessary for external CSS imports to work
                            // https://github.com/facebookincubator/create-react-app/issues/2677
                            ident: 'postcss',
                            plugins: [
                                PostCssFlexbugsFixes,
                                AutoPrefixer({
                                    browsers: [
                                        '>1%',
                                        'last 4 versions',
                                        'Firefox ESR',
                                        'not ie < 9' // React doesn't support IE8 anyway
                                    ],
                                    flexbox: 'no-2009'
                                })
                            ]
                        }
                    }
                ]
            },
            {
                test: /\.(png|jpg|gif|ico|ttf|eot|svg|woff2?)(\?v=[0-9]\.[0-9]\.[0-9])?$/,
                loader: 'file-loader'
            },
            {
                test: /\.json$/,
                loader: 'json-loader'
            }
        ]
    },
    performance: {
        maxEntrypointSize: 1000000,
        maxAssetSize: 1000000,
        hints: false
    },
    mode: mode,
    devtool: (mode === 'development') ? 'source-map' : false,
    devServer: {
        port: 3000,
        historyApiFallback: true,
        public: devServerHost,
        proxy: {
            '/api': apiServerUrl
        },
        open: false
    },
    plugins: [
        new webpack.EnvironmentPlugin({
            NODE_ENV: 'development', // use 'development' unless process.env.NODE_ENV is defined
            DEBUG: false
        }),
        new webpack.HotModuleReplacementPlugin(),
        new WebpackCleanupPlugin(),
        new HtmlWebpackPlugin({
            template: 'public/index.html',
            favicon: 'public/favicon.ico'
        }),
        new ManifestPlugin({
            fileName: "manifest.json"
        })
    ]
};