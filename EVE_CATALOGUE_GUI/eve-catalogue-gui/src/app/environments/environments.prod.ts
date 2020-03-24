// This file can be replaced during build by using the `fileReplacements` array.
// `ng build --prod` replaces `environment.ts` with `environment.prod.ts`.
// The list of file replacements can be found in `angular.json`.
​
export const environment = {
    production: true,
    portalBaseUrl: 'http://10.30.8.33:8082/portal/catalogue/',
    lcmBaseUrl: 'http://10.30.8.33:8083/portal/elm/',
    rbacBaseUrl: 'http://localhost:8888/portal/rbac/',
    ibnBaseUrl: 'http://10.20.8.39:8080/Intent/IntentPage.jsp',
    apiUrl: 'http://10.30.8.33',
    backServerUrl: ''
};
​
/*
 * For easier debugging in development mode, you can import the following file
 * to ignore zone related error stack frames such as `zone.run`, `zoneDelegate.invokeTask`.
 *
 * This import should be commented out in production mode because it will have a negative impact
 * on performance if an error is thrown.
 */
// import 'zone.js/dist/zone-error';  // Included with Angular CLI.
