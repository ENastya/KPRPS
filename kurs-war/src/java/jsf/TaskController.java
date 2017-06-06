package jsf;

import models.Task;
import jsf.util.JsfUtil;
import jsf.util.PaginationHelper;
import sb.TaskFacade;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;
import javax.faces.model.SelectItem;
import models.Status;

@Named("taskController")
@SessionScoped
public class TaskController implements Serializable {

    private Task current;
    private DataModel items = null;
    private DataModel itemsActive = null;
    @EJB
    private sb.TaskFacade ejbFacade;

    @EJB
    private redmine.AccessRM arm;
    
    private CurrentUser cu;

    @EJB
    private sb.ProcessFacade processFacade;

    private PaginationHelper pagination;
    private int selectedItemIndex;
    private boolean active = true;

    public TaskController() {
    }

    public Task getSelected() {
        if (current == null) {
            current = new Task();
            selectedItemIndex = -1;
        }
        return current;
    }

    private TaskFacade getFacade() {
        return ejbFacade;
    }

    public PaginationHelper getPagination() {
        if (pagination == null) {
            pagination = new PaginationHelper(10) {

                private List<Task> TaskList() {
                    try {
                        getArm().ParseXMLtoProject();
                        getArm().ParseXMLtoTask();
                    } catch (UnsupportedEncodingException ex) {
                        Logger.getLogger(TaskController.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    return getFacade().findTaskRange(getCu().getCurUser().getId(), new int[]{getPageFirstItem(), getPageFirstItem() + getPageSize()});
                }

                @Override
                public int getItemsCount() {
                    return TaskList().size();
                }

                @Override
                public DataModel createPageDataModel() {
                    return new ListDataModel(TaskList());

                    //return new ListDataModel(getFacade().getByUserActive(1, true));
                }
            };
        }
        return pagination;
    }

    public String prepareList() {
        setActive(false);
        recreateModel();
        return "List";
    }

    /*   public String prepareActiveList(){ 
        setActive(true);
        recreateModel();
        return "ActiveList";
    }*/
    public String prepareView() {
        current = (Task) getItems().getRowData();
        selectedItemIndex = pagination.getPageFirstItem() + getItems().getRowIndex();
        return "View";
    }

    public String pauseTask(models.Process cur) {
        cur.calculateFullTime();
        cur.setEnded(true);
        getProcessFacade().edit(cur);
        Task task = cur.getTaskId();
        getFacade().editStatus(task, 4);
        return preparePauseView(task);
    }

    public String preparePauseView(Task task) {
        current = task;
        return "TaskView";
    }

    public String prepareCreate() {
        current = new Task();
        selectedItemIndex = -1;
        return "Create";
    }

    public String create() {
        try {
            getFacade().create(current);
            JsfUtil.addSuccessMessage(ResourceBundle.getBundle("/Bundle").getString("TaskCreated"));
            return prepareCreate();
        } catch (Exception e) {
            JsfUtil.addErrorMessage(e, ResourceBundle.getBundle("/Bundle").getString("PersistenceErrorOccured"));
            return null;
        }
    }

    public String prepareEdit() {
        current = (Task) getItems().getRowData();
        selectedItemIndex = pagination.getPageFirstItem() + getItems().getRowIndex();
        return "Edit";
    }

    public String update() {
        try {
            getFacade().edit(current);
            JsfUtil.addSuccessMessage(ResourceBundle.getBundle("/Bundle").getString("TaskUpdated"));
            return "View";
        } catch (Exception e) {
            JsfUtil.addErrorMessage(e, ResourceBundle.getBundle("/Bundle").getString("PersistenceErrorOccured"));
            return null;
        }
    }

    public String destroy() {
        current = (Task) getItems().getRowData();
        selectedItemIndex = pagination.getPageFirstItem() + getItems().getRowIndex();
        performDestroy();
        recreatePagination();
        recreateModel();
        return "List";
    }

    public String destroyAndView() {
        performDestroy();
        recreateModel();
        updateCurrentItem();
        if (selectedItemIndex >= 0) {
            return "View";
        } else {
            // all items were removed - go back to list
            recreateModel();
            return "List";
        }
    }

    private void performDestroy() {
        try {
            getFacade().remove(current);
            JsfUtil.addSuccessMessage(ResourceBundle.getBundle("/Bundle").getString("TaskDeleted"));
        } catch (Exception e) {
            JsfUtil.addErrorMessage(e, ResourceBundle.getBundle("/Bundle").getString("PersistenceErrorOccured"));
        }
    }

    private void updateCurrentItem() {
        int count = getFacade().count();
        if (selectedItemIndex >= count) {
            // selected index cannot be bigger than number of items:
            selectedItemIndex = count - 1;
            // go to previous page if last page disappeared:
            if (pagination.getPageFirstItem() >= count) {
                pagination.previousPage();
            }
        }
        if (selectedItemIndex >= 0) {
            current = getFacade().findRange(new int[]{selectedItemIndex, selectedItemIndex + 1}).get(0);
        }
    }

    public DataModel getItems() {
        recreateModel();
        if (items == null) {
            items = getPagination().createPageDataModel();
        }
        return items;
    }

    private void recreateModel() {
        items = null;
        //getItems();
    }

    private void recreatePagination() {
        pagination = null;
    }

    public String next() {
        getPagination().nextPage();
        recreateModel();

        //FacesContext.getCurrentInstance().getExternalContext().getRequestMap().put("type", "active");
        return "taskList";
    }

    public String previous() {
        getPagination().previousPage();
        recreateModel();
        //FacesContext.getCurrentInstance().getExternalContext().getRequestMap().put("type", "active");

        return "taskList";
    }

    public SelectItem[] getItemsAvailableSelectMany() {
        return JsfUtil.getSelectItems(ejbFacade.findAll(), false);
    }

    public SelectItem[] getItemsAvailableSelectOne() {
        return JsfUtil.getSelectItems(ejbFacade.findAll(), true);
    }

    public Task getTask(java.lang.Integer id) {
        return ejbFacade.find(id);
    }

    /**
     * @return the active
     */
    public boolean isActive() {
        return active;
    }

    public String activeString() {
        if (active) {
            return "active";
        }
        return "";
    }

    /**
     * @param active the active to set
     */
    public void setActive(boolean active) {
        this.active = active;
    }

    /**
     * @return the itemsActive
     */
    public DataModel getItemsActive() {
        if (itemsActive == null) {
            itemsActive = new ListDataModel(getFacade().findActive(getCu().getCurUser().getId()));
        }
        return itemsActive;
    }

    /**
     * @param itemsActive the itemsActive to set
     */
    public void setItemsActive(DataModel itemsActive) {
        this.itemsActive = itemsActive;
    }

    /**
     * @return the statusFacade
     */
    /**
     * @return the processFacade
     */
    public sb.ProcessFacade getProcessFacade() {
        return processFacade;
    }

    /**
     * @return the arm
     */
    private redmine.AccessRM getArm() {
        return arm;
    }

    /**
     * @return the cu
     */
    public CurrentUser getCu() {
        return cu;
    }

    /**
     * @param cu the cu to set
     */
    public void setCu(CurrentUser cu) {
        this.cu = cu;
    }

    @FacesConverter(forClass = Task.class)
    public static class TaskControllerConverter implements Converter {

        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            TaskController controller = (TaskController) facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "taskController");
            return controller.getTask(getKey(value));
        }

        java.lang.Integer getKey(String value) {
            java.lang.Integer key;
            key = Integer.valueOf(value);
            return key;
        }

        String getStringKey(java.lang.Integer value) {
            StringBuilder sb = new StringBuilder();
            sb.append(value);
            return sb.toString();
        }

        @Override
        public String getAsString(FacesContext facesContext, UIComponent component, Object object) {
            if (object == null) {
                return null;
            }
            if (object instanceof Task) {
                Task o = (Task) object;
                return getStringKey(o.getId());
            } else {
                throw new IllegalArgumentException("object " + object + " is of type " + object.getClass().getName() + "; expected type: " + Task.class.getName());
            }
        }

    }

}
