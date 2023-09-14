create table main.v_flow
(
  id          integer           not null
    constraint v_flow_pk
      primary key autoincrement,
  name        TEXT              not null,
  status      integer default 0 not null,
  description TEXT,
  createTime  text              not null,
  updateTime  text              not null
);

create table main.v_plugin
(
  id          integer           not null
    constraint v_plugin_pk
      primary key autoincrement,
  type        TEXT              not null,
  name        TEXT              not null,
  class       TEXT              not null,
  args        TEXT              not null,
  description TEXT              not null,
  status      integer default 0 not null,
  createTime  TEXT              not null,
  updateTime  text              not null,
  script     text
);

create table main.v_verticle
(
  id         integer           not null
    constraint v_verticle_pk
      primary key autoincrement,
  flow_id    integer           not null,
  type       text              not null,
  class      TEXT              not null,
  name       TEXT              not null,
  args       TEXT              not null,
  status     integer default 0 not null,
  createTime text              not null,
  updateTime text              not null,
  script     text
);

