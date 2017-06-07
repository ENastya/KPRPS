package jsf;

import models.Process;
import jsf.util.JsfUtil;
import jsf.util.PaginationHelper;
import sb.ProcessFacade;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;
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
import models.Task;
import models.Text;
import sb.CurrentUser;

@Named("processController")
@SessionScoped
public class ProcessController implements Serializable {

    private Process current;
    private DataModel items = null;
    @EJB
    private sb.ProcessFacade ejbFacade;
    @EJB
    private CurrentUser cu;
    
    @EJB
    private sb.TaskFacade taskFacade;

    private PaginationHelper pagination;
    private int selectedItemIndex;

    public ProcessController() {
    }
    
    
    
    public Process getSelected() {
        if (current == null) {
            current = new Process();
            selectedItemIndex = -1;
        }
        return current;
    }

    private ProcessFacade getFacade() {
        return ejbFacade;
    }

    public PaginationHelper getPagination() {
        if (pagination == null) {
            pagination = new PaginationHelper(10) {

                @Override
                public int getItemsCount() {
                    return getFacade().count();
                }

                @Override
                public DataModel createPageDataModel() {
                    return new ListDataModel(getFacade().userTaskByDate(getCu().getCurUser().getId()));
                }
            };
        }
        return pagination;
    }

    public ListDataModel<Process> findByProj(int id) {
        return new ListDataModel(getFacade().procByProj(id));
    }

    public ListDataModel<Process> findByTask(int id) {
        return new ListDataModel(getFacade().findEndedProcess(id));
    }

    public ListDataModel<Process> findByUser(int id) {
        return new ListDataModel(getFacade().userTaskByDate(id));
    }

    public String fullTime(ListDataModel<Process> processes) {
        long full = 0;
        for (Process i : processes) {
            full += i.getFullTime().getTime();
        }
        return String.format("%02d:%02d:%02d", full / 1000 / 3600, full / 1000 / 60 % 60, full / 1000 % 60);
    }

    public String prepareList() {
        recreateModel();
        return "List";
    }

    public String prepareView() {
        current = (Process) getItems().getRowData();
        current.calculateFullTime();
        selectedItemIndex = pagination.getPageFirstItem() + getItems().getRowIndex();
        return "View";
    }

    public String prepareCreate() {
        current = new Process();
        selectedItemIndex = -1;
        return "Create";
    }

    public String create(Task task) {
        try {
            //current = null;

            getTaskFacade().editStatus(task, 2);

            Process newCurrent = getFacade().findActiveProcess(task.getId());

            if (newCurrent == null) {
                newCurrent = new Process();
                newCurrent.setTaskId(task);
                long curTime = System.currentTimeMillis();
                Date curDate = new Date(curTime);
                newCurrent.setBTime(curDate);
                newCurrent.calculateFullTime();
                getFacade().create(newCurrent);
            }

            JsfUtil.addSuccessMessage(ResourceBundle.getBundle("/Bundle").getString("ProcessCreated"));

            return startTask(newCurrent);
        } catch (Exception e) {
            JsfUtil.addErrorMessage(e, ResourceBundle.getBundle("/Bundle").getString("PersistenceErrorOccured"));
            return null;
        }
    }

    public boolean mayStart(int id) {
        return getFacade().findUserActiveProcess(id) == null;
    }

    public String startTask(Process cur) {
        current = cur;
        //current.calculateFullTime();
        //selectedItemIndex = pagination.getPageFirstItem() + getItems().getRowIndex();

        return "Start";
    }

    public String prepareEdit() {
        current = (Process) getItems().getRowData();
        current.calculateFullTime();
        selectedItemIndex = pagination.getPageFirstItem() + getItems().getRowIndex();
        return "Edit";
    }

    public String update() {
        try {
            getFacade().edit(current);
            JsfUtil.addSuccessMessage(ResourceBundle.getBundle("/Bundle").getString("ProcessUpdated"));
            return "View";
        } catch (Exception e) {
            JsfUtil.addErrorMessage(e, ResourceBundle.getBundle("/Bundle").getString("PersistenceErrorOccured"));
            return null;
        }
    }

    public String destroy() {
        current = (Process) getItems().getRowData();
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
            JsfUtil.addSuccessMessage(ResourceBundle.getBundle("/Bundle").getString("ProcessDeleted"));
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
        if (items == null) {
            items = getPagination().createPageDataModel();
        }
        return items;
    }

    private void recreateModel() {
        items = null;
    }

    private void recreatePagination() {
        pagination = null;
    }

    public String next() {
        getPagination().nextPage();
        recreateModel();
        return "List";
    }

    public String previous() {
        getPagination().previousPage();
        recreateModel();
        return "List";
    }

    public SelectItem[] getItemsAvailableSelectMany() {
        return JsfUtil.getSelectItems(ejbFacade.findAll(), false);
    }

    public SelectItem[] getItemsAvailableSelectOne() {
        return JsfUtil.getSelectItems(ejbFacade.findAll(), true);
    }

    public Process getProcess(java.lang.Integer id) {
        return ejbFacade.find(id);
    }

    /**
     * @return the taskFacade
     */
    public sb.TaskFacade getTaskFacade() {
        return taskFacade;
    }

    /**
     * @param taskFacade the taskFacade to set
     */
    public void setTaskFacade(sb.TaskFacade taskFacade) {
        this.taskFacade = taskFacade;
    }

    /**
     * @return the cu
     */
    public CurrentUser getCu() {
        return cu;
    }

    @FacesConverter(forClass = Process.class)
    public static class ProcessControllerConverter implements Converter {

        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            ProcessController controller = (ProcessController) facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "processController");
            return controller.getProcess(getKey(value));
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
            if (object instanceof Process) {
                Process o = (Process) object;
                return getStringKey(o.getId());
            } else {
                throw new IllegalArgumentException("object " + object + " is of type " + object.getClass().getName() + "; expected type: " + Process.class.getName());
            }
        }

    }

}
