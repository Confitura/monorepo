import {platformBrowserDynamic} from "@angular/platform-browser-dynamic";
import {AppModule} from "./app/app.module";
import {enableProdMode} from "@angular/core";
console.log(ENV);
if (ENV == "prod") {
    console.log("PROD enabled!");
    enableProdMode();
}
const platform = platformBrowserDynamic();
platform.bootstrapModule(AppModule);
