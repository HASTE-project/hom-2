# Connect to the server forwarding ports to these ports on the server:
#  localhost:10443 (for the microK8s dash)
#  localhost:80 (for the HTTP services)
# See: https://www.ibm.com/support/pages/what-are-ssh-tunnels-and-how-use-them

# Tested on Ubuntu 20.04



microk8s status --wait-ready

microk8s enable dns ingress rbac
microk8s status --wait-ready

sudo apt -y install git
git clone --depth 1 https://github.com/HASTE-project/hom-2.git

# Deploy the dashboard
# https://kubernetes.io/docs/tasks/access-application-cluster/web-ui-dashboard/
microk8s kubectl apply -f https://raw.githubusercontent.com/kubernetes/dashboard/v2.5.0/aio/deploy/recommended.yaml
microk8s status --wait-ready

# Create a sample user for the dashboard
# https://github.com/kubernetes/dashboard/blob/master/docs/user/access-control/creating-sample-user.md
microk8s kubectl apply -f hom-2/kubernetes/dashboard-admin.yaml
microk8s status --wait-ready

# Create a print a token for the dashboard user
echo "--- BEGIN DASHBOARD TOKEN ---"
microk8s kubectl -n kubernetes-dashboard create token admin-user
echo "--- END DASHBOARD TOKEN ---"

# Proxy to the dashboard
microk8s kubectl proxy &


microk8s kubectl create namespace hom
microk8s kubectl config set-context --current --namespace=hom
microk8s status --wait-ready


# modify the persistent volume to match the current machine (check the host and path)
# attempt this with sed..
sed -i s/hom-2-benblamey/$(hostname)/ hom-2/kubernetes/storage.yaml
sed -i s+/home/ubuntu/mnt+$(pwd)+ hom-2/kubernetes/storage.yaml

microk8s kubectl apply -f hom-2/kubernetes/storage.yaml
microk8s status --wait-ready

# deploy the remaining resources
microk8s kubectl apply -f hom-2/kubernetes/k8.yaml
microk8s status --wait-ready

# See if everything is running:
microk8s kubectl get all --all-namespaces

# port forward the web ingress to localhost (in the background):
# For for ubuntu+microK8s:
microk8s kubectl port-forward --namespace=ingress daemonset.apps/nginx-ingress-microk8s-controller 8080:80 &
# For Docker on a Mac:
# sudo kubectl port-forward --namespace=ingress-nginx service/ingress-nginx-controller 8080:80
# For something else ?!:
# microk8s kubectl port-forward web-ingress 8080:80 &

echo -- HOM is Ready! --