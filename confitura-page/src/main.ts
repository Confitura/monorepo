import {platformBrowserDynamic} from "@angular/platform-browser-dynamic";
import {AppModule} from "./app/app.module";
import {enableProdMode} from "@angular/core";
import "./vendor";
// import "./polyfills";

if (ENV == "prod") {
    console.log("PROD enabled!");
    enableProdMode();
}
platformBrowserDynamic().bootstrapModule(AppModule);
