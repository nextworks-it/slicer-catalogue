# Portal Catalogue installer

This playbook will install and execute Portal Catalogue Application.

## Requirements

Ansible must be installed on your system:

```sh
apt install software-properties-common
apt-add-repository --yes --update ppa:ansible/ansible
apt install ansible
```

## Configuration

Ansible playbook variables, such as working directory and default user, can be configured through dependency_vars.yml file inside `/vars`.

Portal Catalogue Application and its service can be configured through application.properties and portal-catalogue.service files inside `/roles/portal_catalogyue/templates`.

## Deployment 

To execute playbook:

```sh
ansible-playbook deploy.yml -K
```

This will install the following:
- Java 8
- PostgreSql
- RabbitMq

This playbook will start the PostgreSql and RabbitMq services. In addition, it creates a service also for Portal Catalogue Application named portal-catalogue.service.

To check Portal Catalogue Application status

```sh
service portal-catalogue status
```