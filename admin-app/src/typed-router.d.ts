/* eslint-disable */
/* prettier-ignore */
// @ts-nocheck
// Generated by unplugin-vue-router. ‼️ DO NOT MODIFY THIS FILE ‼️
// It's recommended to commit this file.
// Make sure to add this file to your tsconfig.json file as an "includes" or "files" entry.

declare module 'vue-router/auto-routes' {
  import type {
    RouteRecordInfo,
    ParamValue,
    ParamValueOneOrMore,
    ParamValueZeroOrMore,
    ParamValueZeroOrOne,
  } from 'vue-router'

  /**
   * Route name map generated by unplugin-vue-router
   */
  export interface RouteNamedMap {
    '/': RouteRecordInfo<'/', '/', Record<never, never>, Record<never, never>>,
    '/[...all]': RouteRecordInfo<'/[...all]', '/:all(.*)', { all: ParamValue<true> }, { all: ParamValue<false> }>,
    '/dashboard': RouteRecordInfo<'/dashboard', '/dashboard', Record<never, never>, Record<never, never>>,
    '/homepage': RouteRecordInfo<'/homepage', '/homepage', Record<never, never>, Record<never, never>>,
    '/login': RouteRecordInfo<'/login', '/login', Record<never, never>, Record<never, never>>,
    '/login.[provider]': RouteRecordInfo<'/login.[provider]', '/login/:provider', { provider: ParamValue<true> }, { provider: ParamValue<false> }>,
    '/nested': RouteRecordInfo<'/nested', '/nested', Record<never, never>, Record<never, never>>,
    '/nested/menu1': RouteRecordInfo<'/nested/menu1', '/nested/menu1', Record<never, never>, Record<never, never>>,
    '/nested/menu2': RouteRecordInfo<'/nested/menu2', '/nested/menu2', Record<never, never>, Record<never, never>>,
    '/nested/menu2/menu2-1': RouteRecordInfo<'/nested/menu2/menu2-1', '/nested/menu2/menu2-1', Record<never, never>, Record<never, never>>,
    '/nested/menu2/menu2-2': RouteRecordInfo<'/nested/menu2/menu2-2', '/nested/menu2/menu2-2', Record<never, never>, Record<never, never>>,
    '/register': RouteRecordInfo<'/register', '/register', Record<never, never>, Record<never, never>>,
    '/table': RouteRecordInfo<'/table', '/table', Record<never, never>, Record<never, never>>,
  }
}
