
-----------------------------------------------------------------------
create table v_plugin
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
    updateTime  text              not null
);

INSERT INTO v_plugin (id, type, name, class, args, description, status, createTime, updateTime) VALUES (1, 'httpServer', 'Http服务器', 'com.github.blanexie.vxph.plugin.inbund.HttpServerPlugin', '{"path":"/api","port":8816}', '服务器插件', 1, '2023-09-20 18:36:00', '2023-09-20 18:36:00');

---------------------------------------------------
create table main.v_properties
(
    id          integer not null
        constraint v_properties_pk
            primary key autoincrement,
    key         text    not null,
    value       TEXT    not null,
    create_time TEXT    not null,
    update_time text    not null,
    version     integer,
    status      integer
);

