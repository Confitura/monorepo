function buildConfig(env) {
    return require('./config/webpack.' + env + '.js');
}

module.exports = buildConfig;