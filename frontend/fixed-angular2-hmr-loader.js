// TODO: This is a temporary fix until https://github.com/AngularClass/angular2-hmr-loader/pull/3 is merged.
// This is a copy of https://github.com/AngularClass/angular2-hmr-loader/blob/master/index.js
// with additional `any` annotations to make the TypeScript compiler happy without
// removing the `noImplicitAny` option.

var utils = require('loader-utils');

var bootstrapModule = /(\.bootstrapModule|\.bootstrapModuleFactory)\((.+)\)/gm;
var bootLoader = /((hmr_1|r)\.bootloader)\((.+)\)/gm;

function Angular2HMRLoader(source, sourcemap) {
  var self = this;
  // Not cacheable during unit tests;
  self.cacheable && self.cacheable();
  var query = utils.parseQuery(self.query);

  function done(src, srcmap) {
    // Support for tests
    if (self.callback) {
      self.callback(null, src, srcmap);
    } else {
      return src;
    }
  }

  if (query.prod) {
    source = source.replace(bootLoader, function (match, boot, ngmodule, main, offset, src) {
        // return updated metadata
        return '(document.readyState === "complete") ? ' + main +
        '() : document.addEventListener("DOMContentLoaded", function() { ' + main + '()' + ' })';
      });
    return done(source, sourcemap);
  }

  source = source.replace(bootstrapModule, function (match, boot, ngmodule, offset, src) {
    // return updated metadata
    var newLine = ' ';
    if (query.pretty) {
      newLine = '\n';
    }

    return boot + '(' + ngmodule + ')' +
    '.then(function(MODULE_REF: any) {'+ newLine +
    '  if ((<any>module)["hot"]) {'+ newLine +
    '    (<any>module)["hot"]["accept"]();'+ newLine +
    '    '+ newLine +
    '    if (MODULE_REF.instance["hmrOnInit"]) {'+ newLine +
    '      (<any>module)["hot"]["data"] && MODULE_REF.instance["hmrOnInit"]((<any>module)["hot"]["data"]);'+ newLine +
    '    }'+ newLine +
    '    if (MODULE_REF.instance["hmrOnStatus"]) {'+ newLine +
    '      (<any>module)["hot"]["apply"](function(status: any) {'+ newLine +
    '        MODULE_REF.instance["hmrOnStatus"](status);'+ newLine +
    '      });'+ newLine +
    '    }'+ newLine +
    '    if (MODULE_REF.instance["hmrOnCheck"]) {'+ newLine +
    '      (<any>module)["hot"]["check"](function(err: any, outdatedModules: any) {'+ newLine +
    '        MODULE_REF.instance["hmrOnCheck"](err, outdatedModules);'+ newLine +
    '      });'+ newLine +
    '    }'+ newLine +
    '    if (MODULE_REF.instance["hmrOnDecline"]) {'+ newLine +
    '      (<any>module)["hot"]["decline"](function(dependencies: any) {'+ newLine +
    '        MODULE_REF.instance["hmrOnDecline"](dependencies);'+ newLine +
    '      });'+ newLine +
    '    }'+ newLine +
    '    (<any>module)["hot"]["dispose"](function(store: any) {'+ newLine +
    '      MODULE_REF.instance["hmrOnDestroy"] && MODULE_REF.instance["hmrOnDestroy"](store);'+ newLine +
    '      MODULE_REF.destroy();'+ newLine +
    '      MODULE_REF.instance["hmrAfterDestroy"] && MODULE_REF.instance["hmrAfterDestroy"](store);'+ newLine +
    '    });'+ newLine +
    '  }'+ newLine +
    '  return MODULE_REF;'+ newLine +
    '})'
  });

  return done(source, sourcemap)
};

Angular2HMRLoader.default = Angular2HMRLoader;

module.exports = Angular2HMRLoader
