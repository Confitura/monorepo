/* @ds-bundle: {"format":4,"namespace":"Confitura","components":[{"name":"Button"},{"name":"Tag"},{"name":"SectionHeading"},{"name":"SiteHeader"},{"name":"Tabs"},{"name":"SessionCard"},{"name":"SpeakerCard"},{"name":"TicketCard"},{"name":"Callout"},{"name":"StatGrid"},{"name":"PartnerTier"}]} */
(function () {
  'use strict';

  function h() {
    var R = window.React;
    return R.createElement.apply(R, arguments);
  }
  function cx() {
    var out = [];
    for (var i = 0; i < arguments.length; i++) if (arguments[i]) out.push(arguments[i]);
    return out.join(' ');
  }
  function omit(obj, keys) {
    var o = {};
    for (var k in obj) if (Object.prototype.hasOwnProperty.call(obj, k) && keys.indexOf(k) < 0) o[k] = obj[k];
    return o;
  }
  function initialsOf(name) {
    return String(name || '')
      .split(/\s+/)
      .filter(Boolean)
      .map(function (p) { return p.charAt(0); })
      .slice(0, 2)
      .join('')
      .toUpperCase();
  }

  var KIND = { talk: 'Talk', workshop: 'Workshop', keynote: 'Keynote', panel: 'Panel' };
  var LANG = { PL: 'In Polish', EN: 'In English' };
  var STATUS = {
    available: ['gooseberry', 'Available'],
    'few-left': ['apricot', 'Few left'],
    'sold-out': ['neutral', 'Sold out']
  };

  function Button(props) {
    var variant = props.variant || 'primary';
    var rest = omit(props, ['variant', 'size', 'className', 'children', 'href']);
    rest.className = cx('cf-btn', 'cf-btn-' + variant, props.size === 'lg' && 'cf-btn-lg', props.className);
    if (props.href) {
      rest.href = props.href;
      return h('a', rest, props.children);
    }
    if (!rest.type) rest.type = 'button';
    return h('button', rest, props.children);
  }

  function Tag(props) {
    var tone = props.tone || 'neutral';
    var variant = props.variant || 'soft';
    var marker = props.marker != null ? props.marker : (tone !== 'neutral' && variant === 'soft');
    return h('span', { className: cx('cf-tag', 'cf-tag-' + variant, 'cf-tone-' + tone, props.className), title: props.title },
      marker ? h('span', { className: 'cf-marker', 'aria-hidden': 'true' }) : null,
      props.children);
  }

  function SectionHeading(props) {
    var level = props.level || 2;
    return h('header', { className: cx('cf-sh', props.align === 'center' && 'cf-sh-center', props.className) },
      h('span', { className: 'cf-lid', 'aria-hidden': 'true' }),
      props.eyebrow ? h('p', { className: 'cf-sh-eyebrow' }, props.eyebrow) : null,
      h('h' + level, { className: 'cf-sh-title' }, props.title),
      props.lead ? h('p', { className: 'cf-sh-lead' }, props.lead) : null);
  }

  function SiteHeader(props) {
    var links = props.links || [];
    return h('header', { className: cx('cf-header', props.className) },
      h('a', { className: 'cf-header-home', href: props.homeHref || '/', 'aria-label': 'Confitura home' }, props.logo),
      h('nav', { className: 'cf-header-nav', 'aria-label': 'Main' },
        links.map(function (l, i) {
          return h('a', { key: i, href: l.href, className: 'cf-header-link', 'aria-current': l.current ? 'page' : undefined }, l.label);
        })),
      h('div', { className: 'cf-header-end' },
        props.date ? h('time', { className: 'cf-header-date', dateTime: props.dateTime }, props.date) : null,
        props.cta ? h(Button, { href: props.cta.href, variant: 'primary' }, props.cta.label) : null));
  }

  function SessionCard(props) {
    var tone = props.tone || 'jam';
    var speakers = props.speakers || [];
    var kind = props.kind || 'talk';
    var time = props.start + (props.end ? '–' + props.end : '');
    return h('article', { className: cx('cf-session', 'cf-tone-' + tone, kind === 'keynote' && 'cf-session-keynote', props.className) },
      h('span', { className: 'cf-card-lid', 'aria-hidden': 'true' }),
      h('div', { className: 'cf-card-body' },
        h('div', { className: 'cf-session-meta' },
          h('time', { className: 'cf-time' }, time),
          props.room ? h('span', { className: 'cf-session-room' }, h('span', { className: 'cf-marker', 'aria-hidden': 'true' }), props.room) : null),
        h('h3', { className: 'cf-session-title' }, props.href ? h('a', { href: props.href }, props.title) : props.title),
        speakers.length ? h('ul', { className: 'cf-session-speakers' }, speakers.map(function (s, i) {
          return h('li', { key: i },
            h('span', { className: 'cf-session-speaker' }, s.name),
            s.company ? h('span', { className: 'cf-session-company' }, s.company) : null);
        })) : null,
        h('div', { className: 'cf-session-tags' },
          props.lang ? h(Tag, { variant: 'outline', title: LANG[props.lang] }, props.lang) : null,
          kind !== 'talk' ? h(Tag, null, KIND[kind] || kind) : null,
          props.level ? h(Tag, null, props.level) : null)));
  }

  function SpeakerCard(props) {
    var initials = props.initials || initialsOf(props.name);
    return h('figure', { className: cx('cf-speaker', props.className) },
      h('div', { className: 'cf-jar' },
        h('span', { className: 'cf-jar-lid', 'aria-hidden': 'true' }),
        h('div', { className: 'cf-jar-body' },
          props.photo
            ? h('img', { src: props.photo, alt: '' })
            : h('span', { className: 'cf-jar-initials', 'aria-hidden': 'true' }, initials))),
      h('figcaption', { className: 'cf-speaker-caption' },
        h('span', { className: 'cf-speaker-name' }, props.href ? h('a', { href: props.href }, props.name) : props.name),
        props.company ? h('span', { className: 'cf-speaker-company' }, props.company) : null,
        props.talk ? h('span', { className: 'cf-speaker-talk' }, props.talk) : null));
  }

  function TicketCard(props) {
    var status = props.status || 'available';
    var st = STATUS[status] || STATUS.available;
    var includes = props.includes || [];
    var excludes = props.excludes || [];
    return h('article', { className: cx('cf-ticket', props.featured && 'cf-ticket-featured', props.className) },
      h('span', { className: 'cf-card-lid', 'aria-hidden': 'true' }),
      h('div', { className: 'cf-card-body' },
        h('div', { className: 'cf-ticket-head' },
          h('p', { className: 'cf-ticket-duration' }, props.duration),
          h(Tag, { tone: st[0] }, st[1])),
        h('h3', { className: 'cf-ticket-name' }, props.name),
        props.price ? h('p', { className: 'cf-ticket-price' }, props.price) : null,
        h('ul', { className: 'cf-ticket-list' },
          includes.map(function (t, i) { return h('li', { key: 'in' + i, className: 'cf-in' }, t); }),
          excludes.map(function (t, i) {
            return h('li', { key: 'out' + i, className: 'cf-out' }, h('span', { className: 'cf-sr' }, 'Not included: '), t);
          })),
        props.note ? h('p', { className: 'cf-ticket-note' }, props.note) : null,
        props.cta && status !== 'sold-out'
          ? h(Button, { href: props.cta.href, variant: props.featured ? 'primary' : 'secondary', className: 'cf-ticket-cta' }, props.cta.label)
          : null));
  }

  function Callout(props) {
    return h('aside', { className: cx('cf-callout', 'cf-callout-' + (props.tone || 'jam'), props.className), role: 'note' },
      h('p', { className: 'cf-callout-title' }, props.title || 'Note!'),
      h('div', { className: 'cf-callout-body' }, props.children));
  }

  function StatGrid(props) {
    var items = props.items || [];
    var band = props.variant !== 'plain';
    return h('section', { className: cx('cf-stats', band && 'cf-stats-band', props.className), 'aria-label': typeof props.title === 'string' ? props.title : undefined },
      props.title ? h('h2', { className: 'cf-stats-title' }, props.title) : null,
      props.lead ? h('p', { className: 'cf-stats-lead' }, props.lead) : null,
      h('dl', { className: 'cf-stats-grid' }, items.map(function (it, i) {
        return h('div', { key: i, className: 'cf-stat' },
          h('dt', { className: 'cf-stat-label' }, it.label),
          h('dd', { className: 'cf-stat-value' },
            it.icon ? h('img', { className: 'cf-stat-icon', src: it.icon, alt: '' }) : null,
            h('span', null, it.value)));
      })));
  }

  function Tabs(props) {
    var R = window.React;
    var items = props.items || [];
    var controlled = props.selected != null;
    var state = R.useState(props.defaultSelected != null ? props.defaultSelected : (items[0] && (items[0].value != null ? items[0].value : items[0].label)));
    var current = controlled ? props.selected : state[0];
    return h('div', { className: cx('cf-tabs', props.className), role: 'tablist', 'aria-label': props.label },
      items.map(function (it, i) {
        var value = it.value != null ? it.value : it.label;
        var on = value === current;
        return h('button', {
          key: i, type: 'button', role: 'tab', className: 'cf-tab', 'aria-selected': on ? 'true' : 'false', tabIndex: on ? 0 : -1,
          onClick: function () { if (!controlled) state[1](value); if (props.onSelect) props.onSelect(value); }
        }, it.label);
      }));
  }

  function PartnerTier(props) {
    var partners = props.partners || [];
    var size = props.size || 'md';
    return h('section', { className: cx('cf-tier', 'cf-tier-' + size, props.className) },
      h('h3', { className: 'cf-tier-name' }, h('span', { className: 'cf-marker', 'aria-hidden': 'true' }), props.tier),
      h('ul', { className: 'cf-tier-grid' }, partners.map(function (p, i) {
        var inner = p.logo ? h('img', { src: p.logo, alt: p.name }) : h('span', { className: 'cf-tier-fallback' }, p.name);
        return h('li', { key: i, className: 'cf-tier-slot' }, p.href ? h('a', { href: p.href }, inner) : inner);
      })));
  }

  var api = {
    Button: Button,
    Tag: Tag,
    SectionHeading: SectionHeading,
    SiteHeader: SiteHeader,
    Tabs: Tabs,
    SessionCard: SessionCard,
    SpeakerCard: SpeakerCard,
    TicketCard: TicketCard,
    Callout: Callout,
    StatGrid: StatGrid,
    PartnerTier: PartnerTier
  };
  window.Confitura = Object.assign(window.Confitura || {}, api);
})();
