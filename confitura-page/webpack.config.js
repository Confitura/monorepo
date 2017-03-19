function buildConfig(env) {
    console.log(env);
    return require('./config/webpack.' + env + '.js');
}

module.exports = buildConfig;