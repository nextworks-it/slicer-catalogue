// This file can be replaced during build by using the `fileReplacements` array.
// `ng build --prod` replaces `environment.ts` with `environment.prod.ts`.
// The list of file replacements can be found in `angular.json`.
​
export const environment = {
    production: false,
    portalBaseUrl: 'http://10.20.8.9:8082/portal/catalogue/',
    lcmBaseUrl: 'http://10.20.8.9:8084/portal/elm/',
    rbacBaseUrl: 'http://10.20.8.19:8090/portal/rbac/', /*portal/rbac/*/
    ibnBaseUrl: 'http://10.20.8.39:8080/Intent/IntentPage.jsp',
    apiUrl: 'http://localhost',
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
